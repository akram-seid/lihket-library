package com.example.application.views.borrow;

import com.example.application.data.entity.BorrowDto;
import com.example.application.data.service.BorrowService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("Borrow History")
@Route(value = "borrows-history", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class BorrowHistoryView extends Div {

    private final Grid<BorrowDto> grid = new Grid<>(BorrowDto.class, false);
    BorrowService service;

    public BorrowHistoryView(BorrowService service) {
        this.service = service;
        grid.addColumn(BorrowDto::getBookTitle).setHeader("Title");
        grid.addColumn(BorrowDto::getFullName).setHeader("Borrower");
        grid.addColumn(BorrowDto::getBorrowDate).setHeader("Borrow Date");
        grid.addColumn(BorrowDto::getReturnDate).setHeader("Return Date");

        grid.setItems(service.returned());
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        add(grid);
    }
}
