package com.accounted4.assetmanager.ui.useraccount;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
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
        Label guestMsg = new Label("<b>Demo account:</b> <i>guest/guest</i>", ContentMode.HTML);
        return new MVerticalLayout(
                new MFormLayout(
                        guestMsg,
                        userAccount,
                        password
                ).withWidth(""),
                getToolbar()
        ).withWidth("");
    }


}
