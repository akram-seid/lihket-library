package com.example.application.views.customerList;

import com.example.application.data.entity.Customer;
import com.example.application.data.service.CustomerService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("Readers")
@Route(value = "customer-list", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class CustomerListView extends VerticalLayout {


    private final Grid<Customer> grid = new Grid<>(Customer.class, false);
    CustomerService service;

    public CustomerListView(CustomerService service) {
        this.service = service;
        grid.addColumn(Customer::getFirstName).setHeader("First Name");
        grid.addColumn(Customer::getLastName).setHeader("Last Name");
        grid.addColumn(Customer::getGender).setHeader("Gender");
        grid.addColumn(Customer::getPhone).setHeader("Phone Number");
        grid.addColumn(Customer::getPhone).setHeader("Occupation");

        grid.setItems(service.findAll());
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        add(grid);
    }


}