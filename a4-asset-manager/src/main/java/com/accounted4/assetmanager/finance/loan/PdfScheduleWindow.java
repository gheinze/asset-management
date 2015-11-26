package com.accounted4.assetmanager.finance.loan;

import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Window;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author gheinze
 */
public class PdfScheduleWindow extends Window {

    private static final AtomicInteger PDFS_GENERATED = new AtomicInteger(0);
    private static final int PIXEL_OFFSET_FOR_NEW_WINDOW = 50;
    private static final int NUMBER_OF_WINDOWS_TO_OFFSET_BEFORE_RESET = 5;


    public PdfScheduleWindow(StreamResource.StreamSource stream) {
        super("Amortization Schedule");
        init(stream);
    }

    private void init(StreamResource.StreamSource stream) {

        String resourceName = String.format("AmortizationSchedule_%s.pdf", Integer.toString(PDFS_GENERATED.incrementAndGet()));
        StreamResource streamResource = new StreamResource(stream, resourceName);
        streamResource.setMIMEType("application/pdf");
        streamResource.setCacheTime(0);  // dynamically generated, file name may have no meaning and so we should not cache by file name

        BrowserFrame browserFrame = new BrowserFrame(resourceName, streamResource);
        browserFrame.setSizeFull();

        setResizable(true);
        setWidth(60, Sizeable.Unit.PERCENTAGE);
        setHeight(70, Sizeable.Unit.PERCENTAGE);
        setLocation();
        setContent(browserFrame);

    }

    private void setLocation() {
        int x = 380;
        int y = 130;
        int offset = (PDFS_GENERATED.get() % NUMBER_OF_WINDOWS_TO_OFFSET_BEFORE_RESET) * PIXEL_OFFSET_FOR_NEW_WINDOW;
        setPositionX(x + offset);
        setPositionY(y + offset);
    }

}
