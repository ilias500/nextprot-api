package org.nextprot.api.web.service.impl;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

@Ignore
public class StreamExporterTest extends WebIntegrationBaseTest {

    @Autowired
    private FluentEntryService fluentEntryService;

    @Autowired
    private VelocityConfig velocityConfig;

    @Test
    public void testXMLExportStream() throws Exception {

        Writer writer = new PrintWriter(System.out);

        NPStreamExporter exporter = new XMLStreamExporter();

        exporter.export(Arrays.asList("NX_P06213", "NX_P01308"), writer, "overview");
        //Mockito.verify(os, Mockito.times(4)).flush();
    }

    @Test
    public void testJSONExportStream() throws Exception {

        Writer writer = new PrintWriter(System.out);

        NPStreamExporter exporter = new JSONStreamExporter();

        exporter.export(Arrays.asList("NX_P06213", "NX_P01308"), writer, "overview");
        //Mockito.verify(os, Mockito.times(4)).flush();
    }

    @Test
    public void testFastaExportStream() throws Exception {

        Writer writer = new PrintWriter(System.out);

        NPStreamExporter exporter = new FastaStreamExporter();

        exporter.export(Arrays.asList("NX_P06213", "NX_P01308"), writer, "overview");
        //Mockito.verify(os, Mockito.times(4)).flush();
    }

    @Test
    public void testPeffExportStream() throws Exception {

        Writer writer = new PrintWriter(System.out);

        NPStreamExporter exporter = new PeffStreamExporter();

        exporter.export(Arrays.asList("NX_P06213", "NX_P01308"), writer, "overview");
        //Mockito.verify(os, Mockito.times(4)).flush();
    }
}