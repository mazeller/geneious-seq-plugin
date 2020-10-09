package isuvdl.seqExporterPlugin;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideGraphSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceListDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideGraphSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentFileExporter;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.DocumentType;
import com.biomatters.geneious.publicapi.plugin.Options;
import jebl.util.CompositeProgressListener;
import jebl.util.ProgressListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A seq exporter that exports assembly data into multiple seq files
 */
public class seqExporter extends DocumentFileExporter {
    public String getFileTypeDescription() {
        return "seq file exporter";
    }

    public String getDefaultExtension() {
        return ".seq";
    }

    //Consider limiting to AB1 files to ensure all fields are filled
    public DocumentSelectionSignature[] getSelectionSignatures() {
        return new DocumentSelectionSignature[]{DocumentSelectionSignature.forNucleotideSequences(1, Integer.MAX_VALUE)};
    }

    @Override
    public void export(File file, AnnotatedPluginDocument[] documents, ProgressListener progressListener, Options options) throws IOException {
        CompositeProgressListener compositeProgressListener = new CompositeProgressListener(progressListener, documents.length);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        try {
            for (AnnotatedPluginDocument document : documents) {
                compositeProgressListener.beginSubtask();
                final List<SequenceDocument> sequences = getSequences(document);
               
                //Check if the file is instance of AB1 chromatogram
                //System.out.print(document instanceof DefaultNucleotideGraphSequence);

                CompositeProgressListener compositeProgressListener2 = new CompositeProgressListener(compositeProgressListener, sequences.size());
                for (SequenceDocument sequence : sequences) {
                    if (progressListener.isCanceled()) {
                        return;
                    }
                    compositeProgressListener2.beginSubtask();
                    exportSequence(out, sequence);
                }
            }
        } finally {
            out.close();
            if (progressListener.isCanceled()) {
                file.delete();
            }
        }
    }

    //Work horse (check if \n works, or \r\n
    private void exportSequence(BufferedWriter out, SequenceDocument sequence) throws IOException {
    	
        out.write("\"" + sequence.getName() + "\" (1," + sequence.getSequenceLength() + ")\n");
        out.write("  Contig Length:                  " + sequence.getSequenceLength() + " bases\n");
        //FUTURE - GRAB ASSEMBLY DATA FROM GENEIOUS AND EXPORT IT INTO THE FILE
        /*out.write("  Average Length/Sequence:        " + sequence.getSequenceLength() + " bases\n");
        out.write("  Total Sequence Length:         0 bases\n");
        out.write("  Top Strand:                       0 sequences\n");
        out.write("  Bottom Strand:                    0 sequences\n");
        out.write("  Total:                            0 sequences\n");
        out.write("FEATURES             Location/Qualifiers\n");
        out.write("     contig          1.." + sequence.getSequenceLength() + "\n");
        out.write("                     /Note=\"Contig 0(1>0)\"\n");
        out.write("                     /dnas_scaffold_ID=0\n");
        out.write("                     /dnas_scaffold_POS=0\n");*/
        out.write("\n");
        out.write("^^\n");
        out.write(sequence.getSequenceString() + "\n");
        out.write("\n");
    }

    private List<SequenceDocument> getSequences(AnnotatedPluginDocument annotatedPluginDocument) throws IOException {
        List<SequenceDocument> sequences = new ArrayList<SequenceDocument>();
        final PluginDocument pluginDocument = annotatedPluginDocument.getDocumentOrThrow(true, ProgressListener.EMPTY, IOException.class);
        if (DocumentType.isSequence(pluginDocument)) {
            SequenceDocument sequenceDocument = (SequenceDocument) pluginDocument;
            sequences.add(sequenceDocument);
        }
        else if (DocumentType.isSequenceList(pluginDocument)) {
            SequenceListDocument sequenceListDocument = (SequenceListDocument) pluginDocument;
            sequences.addAll(sequenceListDocument.getNucleotideSequences());
            sequences.addAll(sequenceListDocument.getAminoAcidSequences());
        }
        return sequences;
    }
}