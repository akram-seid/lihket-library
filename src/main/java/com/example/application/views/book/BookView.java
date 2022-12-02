package com.example.application.views.book;

import com.example.application.data.entity.Book;
import com.example.application.data.service.BookService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Book")
@Route(value = "Book/:bookID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("USER")
public class BookView extends Div implements BeforeEnterObserver {

    private final String BOOK_ID = "bookID";
    private final String BOOK_EDIT_ROUTE_TEMPLATE = "Book/%s/edit";

    private final Grid<Book> grid = new Grid<>(Book.class, false);

    private TextField bookTitle;
    private TextField author;
    private TextField isbn;
    private DatePicker publicationDate;
    private TextField catalogId;
    private TextField gener;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Book> binder;

    private Book book;

    private final BookService bookService;

    @Autowired
    public BookView(BookService bookService) {
        this.bookService = bookService;
        addClassNames("book-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("bookTitle").setAutoWidth(true);
        grid.addColumn("author").setAutoWidth(true);
        grid.addColumn("isbn").setAutoWidth(true);
        grid.addColumn("publicationDate").setAutoWidth(true);
        grid.addColumn("catalogId").setAutoWidth(true);
        grid.addColumn("gener").setAutoWidth(true);
        grid.setItems(query -> bookService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(BOOK_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(BookView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Book.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.book == null) {
                    this.book = new Book();
                }
                binder.writeBean(this.book);
                bookService.update(this.book);
                clearForm();
                refreshGrid();
                Notification.show("Book details stored.");
                UI.getCurrent().navigate(BookView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the book details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> bookId = event.getRouteParameters().get(BOOK_ID).map(UUID::fromString);
        if (bookId.isPresent()) {
            Optional<Book> bookFromBackend = bookService.get(bookId.get());
            if (bookFromBackend.isPresent()) {
                populateForm(bookFromBackend.get());
            } else {
                Notification.show(String.format("The requested book was not found, ID = %s", bookId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(BookView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        bookTitle = new TextField("Book Title");
        author = new TextField("Author");
        isbn = new TextField("Isbn");
        publicationDate = new DatePicker("Publication Date");
        catalogId = new TextField("Catalog Id");
        gener = new TextField("Gener");
        formLayout.add(bookTitle, author, isbn, publicationDate, catalogId, gener);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Book value) {
        this.book = value;
        binder.readBean(this.book);

    }
}
