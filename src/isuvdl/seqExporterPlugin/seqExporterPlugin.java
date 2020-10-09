package isuvdl.seqExporterPlugin;

import com.biomatters.geneious.publicapi.plugin.*;

/**
 * @author Michael Zeller
 */
public class seqExporterPlugin extends GeneiousPlugin {
	
    public String getName() {
        return "ISUVDL Seq File Exporter";
    }

    public String getHelp() {
        return "To be written";
    }

    public String getDescription() {
        return "Export files in .seq format";
    }

    public String getAuthors() {
        return "Iowa State University Veterinary Diagnostic Lab";
    }
    
    public String getEmailAddressForCrashes() {
    	return "mazeller@iastate.edu";
    }

    public String getVersion() {
        return "0.1";
    }

    public String getMinimumApiVersion() {
        return "4.0";
    }

    public int getMaximumApiVersion() {
        return 4;
    }

    @Override
    public DocumentFileExporter[] getDocumentFileExporters() {
        return new DocumentFileExporter[]{new seqExporter()};
    }
}