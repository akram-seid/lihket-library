package com.example.application.views.readerList;

import com.example.application.data.entity.Customer;
import com.example.application.data.service.CustomerService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("Readers")
@Route(value = "readers-list", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ReaderListView extends VerticalLayout {

    private final Grid<Customer> grid = new Grid<>(Customer.class, false);
    CustomerService service;
    private TextField searchField = new TextField();

    public ReaderListView(CustomerService service) {
        this.service = service;

        grid.addColumn(Customer::getId).setHeader("Reader Id");
        grid.addColumn(Customer::getFullName).setHeader("Full Name");
        grid.addColumn(Customer::getGender).setHeader("Gender");
        grid.addColumn(Customer::getPhone).setHeader("Phone Number");
        grid.addColumn(Customer::getOccupation).setHeader("Occupation");

        grid.setItems(service.findAll());
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        GridListDataView<Customer> dataView = grid.setItems(service.findAll());
        searchField.setWidth("40%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(person -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesFullName = matchesTerm(person.getFullName(),
                    searchTerm);
            boolean matchesEmail = matchesTerm(person.getPhone(), searchTerm);


            return matchesFullName || matchesEmail;
        });


        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(searchField, grid);

        add(verticalLayout);
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

}