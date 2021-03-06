package za.ac.sun.cs.st4;

import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.ErrorBuffer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Template {
    /*
     * where to look for templates */
    @Parameter(property = "sourceDirectory", defaultValue = "${basedir}/src/main/resources/templates")
    private File sourceDirectory;
    /*
     * where to keep generated files
     */
//    @Parameter(property = "outputDirectory",defaultValue = "${project.build.directory}/generated-sources/templates")
//    private File outputDirectory;

    /*
     * template file
     **/
    @Parameter(property = "templateFile")
    private String templateFile;
    /*
     * name of the template to process
     **/
    @Parameter(property = "target", defaultValue = "")
    private String target;

    /*
     *  name of generated source file
     * */
    @Parameter(property = "generatedFileName", defaultValue = "")
    private String generatedFileName;
    /*
     * for simple mapping of string key to string value
     **/
    private Map<String, String> properties;

    /*
     * for string to Lists
     **/
    @Parameter(property = "variableList")
    private Map<String, VariableList> variableList;

    public void render(String templateFile, String templateName, String generatedFile, List<String> props) throws IOException {
        if (anyChanges(templateFile, generatedFile)) {
            STGroup group = new STGroupFile(templateFile);
            ST st = group.getInstanceOf(templateName);

            FileWriter writer = new FileWriter(generatedFile);
            ErrorBuffer listener = new ErrorBuffer();

            putMapElements(props);

            for(String key : properties.keySet()) {
                st.add(key, properties.get(key));
            }
            /* dealing with lists in st4 syntax */
            if (variableList != null) {
                for (String key : variableList.keySet()) {
                    st.add(key, variableList.get(key).getVariables());
                }
            }
            st.write(new AutoIndentWriter(writer), listener);
            writer.flush();
            writer.close();
        } else {
            System.out.println("No changes detected! Skipping!!");
        }
    }
    
    private void putMapElements(List<String> props){
        properties = new HashMap<String, String>();
        for (String prop : props) {
            String[] tokens = prop.split("=");
            properties.put(tokens[0], tokens[1]);
        }
    }

    private boolean anyChanges(String templateFile, String outputFile) {
        File temp = new File(templateFile);
        File out = new File(outputFile);

        if (!out.exists()) {
            return true;
        }
        else if( out.lastModified() <= temp.lastModified()) {
            return true;
        }
        return false;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public String getTarget() {
        return target;
    }
    public String getGeneratedFileName() {
        return  generatedFileName;
    }
    private Map<String, String> getProperties() {
        return properties;
    }
}
