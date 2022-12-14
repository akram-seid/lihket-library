package com.example.application.views.borrow;

import com.example.application.data.entity.Borrow;
import com.example.application.data.entity.BorrowDto;
import com.example.application.data.service.BorrowService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("Borrow List")
@Route(value = "Borrows", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class BorrowView extends Div {

    private final Grid<BorrowDto> grid = new Grid<>(BorrowDto.class, false);
    BorrowService service;

    public BorrowView(BorrowService service) {
        this.service = service;
        grid.addColumn(BorrowDto::getBookTitle).setHeader("Title");
        grid.addColumn(BorrowDto::getFullName).setHeader("Borrower");
        grid.addColumn(BorrowDto::getBorrowDate).setHeader("Borrow Date");
        grid.addColumn(BorrowDto::getReturnDate).setHeader("Return Date");
        grid.addComponentColumn(this::returnBook).setHeader("Options");
        grid.setItems(service.unReturned());
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        add(grid);
    }

    public Component returnBook(BorrowDto borrowdto) {
        Borrow borrow = new Borrow();
        borrow.setReturned(true);
        return new Button("Returned", VaadinIcon.CHECK.create(), click -> {
            service.updateById(borrowdto.getId());
            Notification.show("Book returned Successfully!").setPosition(Notification.Position.MIDDLE);
            refreshList();
        });
    }

    private void refreshList() {
        grid.setItems(service.unReturned());
    }
}
