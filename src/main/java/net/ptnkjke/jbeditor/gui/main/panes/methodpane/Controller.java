package net.ptnkjke.jbeditor.gui.main.panes.methodpane;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import net.ptnkjke.jbeditor.gui.main.model.classtree.Method;
import net.ptnkjke.jbeditor.utils.Editor;
import net.ptnkjke.jbeditor.utils.GraphVizCreator;
import net.ptnkjke.jbeditor.utils.JByteParser;
import org.apache.bcel.generic.*;

import java.io.File;
import java.net.MalformedURLException;


/**
 * Created by Lopatin on 05.07.2014.
 */
public class Controller {
    @FXML
    private TextArea textArea;
    @FXML
    private WebView webview;

    private GraphVizCreator graphVizCreator;

    private ClassGen classGen;
    private Method method;

    public void setMethod(Method method) {
        this.method = method;
        classGen = method.getClassGen();
        int num = method.getMethodIndex();

        MethodGen methodGen = new MethodGen(classGen.getMethodAt(num),
                classGen.getClassName(),
                classGen.getConstantPool());

        StringBuilder sb = new StringBuilder();
        InstructionHandle handle = methodGen.getInstructionList().getStart();

        if (handle != null) {
            do {
                Editor editor = new Editor();
                try {
                    editor.visit(handle);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                sb.append(editor.getResult()).append("\n");
                handle = handle.getNext();
            } while (handle != null);
        }

        textArea.setText(sb.toString());

        graphVizCreator = new GraphVizCreator(methodGen.getInstructionList(), classGen.getConstantPool());
        File image = graphVizCreator.getImage();

        if(image != null) {
            try {
                webview.getEngine().load(image.toURI().toURL().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public void testSave() {
        JByteParser jByteParser = new JByteParser();
        ConstantPoolGen constantPoolGen = classGen.getConstantPool();

        jByteParser.parse(textArea.getText(), constantPoolGen);
        InstructionList instructionList = jByteParser.getInstructions();

        int num = method.getMethodIndex();
        org.apache.bcel.classfile.Method old = classGen.getMethodAt(num);

        MethodGen mg = new MethodGen(old, classGen.getClassName(), classGen.getConstantPool());
        mg.removeLineNumbers();
        mg.removeLocalVariables();
        mg.setInstructionList(instructionList);
        classGen.setMethodAt(mg.getMethod(), num);
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }
}
