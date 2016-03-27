package com.accounted4.assetmanager.useraccount;

import com.vaadin.ui.Component;
import org.vaadin.viritin.fields.MPasswordField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 *
 * @author gheinze
 */
public class LoginForm extends AbstractForm<LoginFormBean>  {

    private final MTextField userAccount = new MTextField("Account");
    private final MPasswordField password = new MPasswordField("Password");


    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new MFormLayout(
                        userAccount,
                        password
                ).withWidth(""),
                getToolbar()
        ).withWidth("");
    }


}
