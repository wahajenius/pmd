/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;

class HTMLRendererTest extends AbstractRendererTest {

    @Override
    protected String getSourceCodeFilename() {
        return "someFilename<br>thatNeedsEscaping.ext";
    }

    private String getEscapedFilename() {
        return "someFilename&lt;br&gt;thatNeedsEscaping.ext";
    }

    @Override
    Renderer getRenderer() {
        return new HTMLRenderer();
    }

    @Override
    String getExpected() {
        return getExpected(null, null);
    }

    private String getExpected(String linkPrefix, String lineAnchor) {
        String filename = getEscapedFilename();
        if (linkPrefix != null) {
            filename = "<a href=\"" + linkPrefix + filename + "#" + lineAnchor + "\">"
                    + filename + "</a>";
        }
        return getHeader()
                + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL + "<td align=\"center\">1</td>" + PMD.EOL
                + "<td width=\"*%\">" + filename + "</td>" + PMD.EOL + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL
                + "<td width=\"*\">blah</td>" + PMD.EOL + "</tr>" + PMD.EOL + "</table></body></html>" + PMD.EOL;
    }

    @Override
    String getExpectedEmpty() {
        return getHeader()
                + "</table></body></html>" + PMD.EOL;
    }

    @Override
    String getExpectedMultiple() {
        return getHeader()
                + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL + "<td align=\"center\">1</td>" + PMD.EOL
                + "<td width=\"*%\">" + getEscapedFilename() + "</td>" + PMD.EOL + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL
                + "<td width=\"*\">blah</td>" + PMD.EOL + "</tr>" + PMD.EOL + "<tr> " + PMD.EOL
                + "<td align=\"center\">2</td>" + PMD.EOL + "<td width=\"*%\">" + getEscapedFilename() + "</td>" + PMD.EOL
                + "<td align=\"center\" width=\"5%\">1</td>" + PMD.EOL + "<td width=\"*\">blah</td>" + PMD.EOL + "</tr>"
                + PMD.EOL + "</table></body></html>" + PMD.EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return getHeader()
                + "</table><hr/><center><h3>Processing errors</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>File</th><th>Problem</th></tr>" + PMD.EOL + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL
                + "<td>file</td>" + PMD.EOL + "<td><pre>" + error.getDetail() + "</pre></td>" + PMD.EOL + "</tr>" + PMD.EOL + "</table></body></html>"
                + PMD.EOL;
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return getHeader()
                + "</table><hr/><center><h3>Configuration errors</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>Rule</th><th>Problem</th></tr>" + PMD.EOL + "<tr bgcolor=\"lightgrey\"> " + PMD.EOL
                + "<td>Foo</td>" + PMD.EOL + "<td>a configuration error</td>" + PMD.EOL + "</tr>" + PMD.EOL + "</table></body></html>"
                + PMD.EOL;
    }

    private String getHeader() {
        return "<html><head><title>PMD</title></head><body>" + PMD.EOL
                + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>"
                + PMD.EOL + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>" + PMD.EOL;
    }

    @Test
    void testLinkPrefix() throws IOException {
        final HTMLRenderer renderer = new HTMLRenderer();
        final String linkPrefix = "https://github.com/pmd/pmd/blob/master/";
        final String linePrefix = "L";
        renderer.setProperty(HTMLRenderer.LINK_PREFIX, linkPrefix);
        renderer.setProperty(HTMLRenderer.LINE_PREFIX, Optional.of(linePrefix));
        renderer.setProperty(HTMLRenderer.HTML_EXTENSION, false);

        String actual = renderReport(renderer, reportOneViolation());
        assertEquals(filter(getExpected(linkPrefix, "L1")), filter(actual));
    }

    @Test
    void testLinePrefixNotSet() throws IOException {
        final HTMLRenderer renderer = new HTMLRenderer();
        final String linkPrefix = "https://github.com/pmd/pmd/blob/master/";
        renderer.setProperty(HTMLRenderer.LINK_PREFIX, linkPrefix);
        // dont set line prefix renderer.setProperty(HTMLRenderer.LINE_PREFIX, linePrefix);
        renderer.setProperty(HTMLRenderer.HTML_EXTENSION, false);

        String actual = renderReport(renderer, reportOneViolation());
        assertEquals(filter(getExpected(linkPrefix, "")), filter(actual));
    }

    @Test
    void testEmptyLinePrefix() throws IOException {
        final HTMLRenderer renderer = new HTMLRenderer();
        final String linkPrefix = "https://github.com/pmd/pmd/blob/master/";
        renderer.setProperty(HTMLRenderer.LINK_PREFIX, linkPrefix);
        renderer.setProperty(HTMLRenderer.LINE_PREFIX, Optional.of(""));
        renderer.setProperty(HTMLRenderer.HTML_EXTENSION, false);

        String actual = renderReport(renderer, reportOneViolation());
        assertEquals(filter(getExpected(linkPrefix, "1")), filter(actual));
    }
}
