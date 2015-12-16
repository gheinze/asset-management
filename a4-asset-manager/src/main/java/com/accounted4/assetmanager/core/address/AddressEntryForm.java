package com.accounted4.assetmanager.core.address;

import com.accounted4.assetmanager.util.vaadin.ui.DefaultView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.vaadin.viritin.fields.MTextArea;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 *
 * @author gheinze
 */
@UIScope
@SpringView()
public class AddressEntryForm extends AbstractForm<Address> implements DefaultView {

    private Country defaultCountry = null;
    private CountrySubdivision defaultCountrySubdivision = null;

    private final CountryRepository countryRepo;
    private final CountrySubdivisionRepository countrySubdivisionRepo;

    private final TextField line1 = new MTextField("Line 1");
    private final TextField line2 = new MTextField("Line 2");
    private final TextField city = new MTextField("City");
    private final TextField postalCode = new MTextField("Postal Code");
    private final TextArea note = new MTextArea("Note");
    private final CheckBox inactive = new CheckBox("Inactive");

    private TypedSelect<Country> country;
    private TypedSelect<CountrySubdivision> countrySubdivision;


    @Inject
    public AddressEntryForm(CountryRepository countryRepo, CountrySubdivisionRepository countrySubdivisionRepo) {
        this.countryRepo = countryRepo;
        this.countrySubdivisionRepo = countrySubdivisionRepo;
    }


    public void setAddress(Address address) {
        setEntity(null == address ? createNewAddress() : address);
    }


    @PostConstruct
    private void init() {
        setSizeUndefined();
        prepareCountrySelect();
        prepareCountrySubdivisionSelect();
    }


    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new MFormLayout(
                        line1,
                        line2,
                        city,
                        countrySubdivision,
                        country,
                        postalCode,
                        note,
                        inactive
                ).withWidth(""),
                getToolbar()
        ).withWidth("");
    }


    private void prepareCountrySelect() {
        country = new TypedSelect<>(Country.class);
        country.setCaption("Country");
        country.addMValueChangeListener(e -> countrySelectionChanged());
        country.setBeans(countryRepo.findAll());
        configureDefaultCountry();
    }

    private void prepareCountrySubdivisionSelect() {
        countrySubdivision = new TypedSelect<>(CountrySubdivision.class);
        countrySubdivision.setCaption("Province");
        countrySubdivision.setBeans(countrySubdivisionRepo.findAll());
        configureDefaultCountrySubdivision();
    }

    private Address createNewAddress() {
        Address address = new Address();
        address.setCountry(defaultCountry);
        address.setCountrySubdivision(defaultCountrySubdivision);
        return address;
    }


    private void configureDefaultCountry() {
        Optional<Country> foundCountry = country.getOptions()
                .stream()
                .filter(streamCountry -> streamCountry.getCountryCode().equals("CA"))
                .limit(1)
                .findFirst()
                ;
        defaultCountry = foundCountry.isPresent() ? foundCountry.get() : null;
    }


    private void configureDefaultCountrySubdivision() {
        Optional<CountrySubdivision> foundCountrySubdivision = countrySubdivision.getOptions()
                .stream()
                .filter(streamCountrySubdivision -> streamCountrySubdivision.getSubdivisionCode().equals("ON"))
                .limit(1)
                .findFirst()
                ;
        defaultCountrySubdivision = foundCountrySubdivision.isPresent() ? foundCountrySubdivision.get() : null;
    }

    @SuppressWarnings("unchecked") // Until "TypedSelect" uses generics in it's hierarchy
    private void countrySelectionChanged() {
        countrySubdivision.setBeans(countrySubdivisionRepo.findByCountryOrderBySubdivisionCode(country.getValue()));
        countrySubdivision.setValue(defaultCountrySubdivision);
    }

}
