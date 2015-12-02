package com.accounted4.assetmanager;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Panel;
import javax.annotation.PostConstruct;

/**
 * The Panel into which the active form is to be placed by the Navigator.
 *
 * @author gheinze
 */
@SpringComponent
@UIScope
public class ApplicationContentArea extends Panel {

    @PostConstruct
    public void postConstruct() {
        setSizeFull();
    }

}
