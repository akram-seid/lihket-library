package com.example.application.views.book;

import com.example.application.data.entity.Book;
import com.example.application.data.entity.Borrow;
import com.example.application.data.entity.Customer;
import com.example.application.data.service.BookService;
import com.example.application.data.service.BorrowService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@PageTitle("Books")
@Route(value = "books", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class BookView extends Div implements BeforeEnterObserver {

    private final String BOOK_ID = "bookID";

    private final Grid<Book> grid = new Grid<>(Book.class, false);

    private TextField bookTitle;
    private TextField author;
    private TextField isbn;
    private DatePicker publicationDate;
    private TextField catalogId;
    TextField searchField = new TextField();
    BorrowService borrowService;
    private TextField genera;


    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Book> binder;

    private Book book;

    private final BookService bookService;
    private TextField quantity;

    @Autowired
    public BookView(BookService bookService, BorrowService borrowService) {
        this.bookService = bookService;
        this.borrowService = borrowService;
        addClassNames("book-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        add(searchField);
        add(splitLayout);


        grid.addColumn(Book::getBookTitle).setHeader("bookTitle").setAutoWidth(true);
        grid.addColumn(Book::getAuthor).setHeader("author").setAutoWidth(true);
        grid.addColumn(Book::getCatalogId).setHeader("catalogId").setAutoWidth(true);
        grid.addComponentColumn(this::createLend).setHeader("Option");
        grid.setItems(bookService.findAll());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        GridListDataView<Book> dataView = grid.setItems(bookService.findAll());
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());
        dataView.addFilter(person -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesFullName = matchesTerm(person.getBookTitle(),
                    searchTerm);
            boolean matchesEmail = matchesTerm(person.getIsbn(), searchTerm);
            boolean matchesProfession = matchesTerm(person.getAuthor(),
                    searchTerm);

            return matchesFullName || matchesEmail || matchesProfession;
        });


        // Configure Form
        binder = new BeanValidationBinder<>(Book.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);
        binder.forField(quantity)
                .withConverter(
                        new StringToIntegerConverter("Not a number"))
                .bind(Book::getQuantity,
                        Book::setQuantity);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
            dataView.refreshAll();
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
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the book details.");
            }
        });


    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    public Component createLend(Book book) {
        Button button = new Button("Lend");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(click -> {
            createBorrow(book);
        });
        return button;
    }

    public void createBorrow(Book book) {
        VerticalLayout layout = new VerticalLayout();
        H1 h1 = new H1("Hello Lender");
        Borrow borrow = new Borrow();
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.open();
        dialog.add(new LendForm(() -> {
            dialog.close();
            save(borrow);
        }, book, borrow));

    }

    private void save(Borrow borrow) {
        borrowService.update(borrow);
        Notification.show("You successfully Lent " + borrow.getBook().getBookTitle() +
                " to customer with ID: " + borrow.getCustomer().getId()).setPosition(Notification.Position.MIDDLE);
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> bookId = event.getRouteParameters().get(BOOK_ID).map(Long::valueOf);
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
        genera = new TextField("Genera");
        quantity = new TextField("Quantity");
        formLayout.add(bookTitle, author, isbn, publicationDate, catalogId, genera, quantity);

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
        grid.setItems(bookService.findAll());
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Book value) {
        this.book = value;
        binder.readBean(this.book);

    }
}

class LendForm extends Composite<Component> {
    private final SerializableRunnable saveListener;
    private Book book;
    private Borrow borrow;
    private TextField customerId = new TextField("Customer ID");
    private DatePicker borrowDate = new DatePicker("Borrow Date");
    private DatePicker returnDate = new DatePicker("Return Date");

    public LendForm(SerializableRunnable saveListener, Book book, Borrow borrow1) {
        this.saveListener = saveListener;
        this.book = book;
        this.borrow = borrow1;
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        borrowDate.setMax(now);
        returnDate.setMin(now);


    }

    @Override
    protected Component initContent() {
        return new VerticalLayout(
                new H4("Lend Book ..."),
                new H6("Book: " + book.getBookTitle()),
                customerId,
                borrowDate,
                returnDate,
                new Button("Save", VaadinIcon.CHECK.create(), buttonClickEvent -> {
                    saveClicked();
                })


        );
    }

    private void saveClicked() {
        Customer customer = new Customer();
        customer.setId(Long.valueOf(customerId.getValue()));
        borrow.setCustomer(customer);
        borrow.setBook(book);
        borrow.setBorrowDate(borrowDate.getValue());
        borrow.setReturnDate(returnDate.getValue());
        borrow.setReturned(false);
        saveListener.run();
    }
}
