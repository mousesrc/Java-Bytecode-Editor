package net.ptnkjke.jbeditor.gui.main.constantpanes.table;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.ptnkjke.jbeditor.logic.Core;
import net.ptnkjke.jbeditor.logic.bcel.bytecode.CellConstantWorker;
import net.ptnkjke.jbeditor.logic.own.bytecode.ConstantPool;
import net.ptnkjke.jbeditor.logic.own.bytecode.OClass;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Интересные невероятности. BCEL не имеет возможности удалнеия константы, нет возможности контроирования пулов....
 * Нельзя взять просто так и отредактриовать сущесвующую.. всё это происходид через дополнительные методы.
 */

/**
 * Created by Lopatin on 09.07.2014.
 */
public class Controller {
    @FXML
    private TableView<ConstantTableCell> table;
    @FXML
    private TextArea cpEditor;

    private ClassGen classGen;
    private OClass oClass;

    @FXML
    public void initialize() {
        TableColumn<ConstantTableCell, Integer> column = (TableColumn<ConstantTableCell, Integer>) table.getColumns().get(0);
        column.setCellValueFactory(new PropertyValueFactory<ConstantTableCell, Integer>("id"));

        TableColumn<ConstantTableCell, String> column1 = (TableColumn<ConstantTableCell, String>) table.getColumns().get(1);
        column1.setCellValueFactory(new PropertyValueFactory<ConstantTableCell, String>("type"));

        TableColumn<ConstantTableCell, String> column2 = (TableColumn<ConstantTableCell, String>) table.getColumns().get(2);
        column2.setCellValueFactory(new PropertyValueFactory<ConstantTableCell, String>("value"));
        table.setEditable(true);

        // Добавляем поле, которое будет появляться для редактирования
        column2.setCellFactory(param -> new EditingCell());
        // Автосохранение изменений
        column2.setOnEditCommit(
                t -> {
                    t.getRowValue().setValue(t.getNewValue());
                    t.getRowValue().setChanged(true);
/*                    ((ConstantTableCell) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())).setValue(t.getNewValue());*/
                });
    }

    public void setClassGen(ClassGen cg) {
        this.classGen = cg;
        ConstantPoolGen cpg = cg.getConstantPool();
        int length = cpg.getConstantPool().getLength();
        for (int i = 1; i < length; i++) {
            Constant constant = cpg.getConstant(i);
            if (constant == null) {
                continue;
            }
            ConstantTableCell ctb;

            CellConstantWorker cellConstantWorker = new CellConstantWorker(cpg.getConstantPool());

            try {
                cellConstantWorker.visit(constant);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ctb = cellConstantWorker.getConstantTableCell();
            if (ctb == null) {
                continue;
            }
            ctb.setConst_type(constant.getTag());
            ctb.setId(i);
            table.getItems().add(ctb);
        }

        // Преобразуем константу в текст
        // TODO: А нахуя этот кусок?
      /*  StringBuilder sb = new StringBuilder();
        for (int i = 1; i < length; i++) {
            Constant constant = cpg.getConstant(i);
            if (constant == null) {
                continue;
            }
            TextConstantWorker textConstantWorker = new TextConstantWorker(cpg.getConstantPool());

            try {
                textConstantWorker.visit(constant);
            } catch (Exception e) {
                e.printStackTrace();
            }

            sb.append(textConstantWorker.getText()).append("\n");
        }*/

        oClass = new OClass();
        try {
            oClass.readFromBytes(Core.INSTANCE.getClassMap().get(cg.getClassName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        cpEditor.setText(ConstantPool.getConstantPoolCode(oClass.getConstantPool()));
    }

    // Сохранение констант
    public void saveConstants() throws Exception {

        ConstantPoolGen constantPoolGen =
                classGen.getConstantPool();

        for (ConstantTableCell cell : table.getItems()) {

            if (!cell.isChanged()) {
                continue;
            }

            int old_id = cell.getId();
            int new_id = -1;

            // Получаем новый id-шник
            switch (cell.getConst_type()) {
                case Const.CONSTANT_Utf8:
                    new_id = constantPoolGen.addUtf8(cell.getValue());
                    break;
                case Const.CONSTANT_Integer:
                    new_id = constantPoolGen.addInteger(Integer.parseInt(cell.getValue()));
                    break;
                case Const.CONSTANT_Float:
                    new_id = constantPoolGen.addFloat(Float.parseFloat(cell.getValue()));
                    break;
                case Const.CONSTANT_Long:
                    new_id = constantPoolGen.addLong(Integer.parseInt(cell.getValue()));
                    break;
                case Const.CONSTANT_Double:
                    new_id = constantPoolGen.addDouble(Double.parseDouble(cell.getValue()));
                    break;
                case Const.CONSTANT_Class:
                    // TODO:
                    break;
                case Const.CONSTANT_Fieldref:
                    // TODO:
                    break;
                case Const.CONSTANT_String:
                    new_id = constantPoolGen.addString(cell.getValue());
                    break;
                case Const.CONSTANT_Methodref:
                    // TODO:
                    break;
                case Const.CONSTANT_InterfaceMethodref:
                    // TODO:
                    break;
                case Const.CONSTANT_NameAndType:
                    // TODO:
                    break;
                case Const.CONSTANT_MethodHandle:
                    // TODO:
                //    break;
                case Const.CONSTANT_MethodType:
                    // TODO:
                //    break;
                default:
                    throw new Exception("");
            }

            List<Method> changes = new ArrayList<>();

            // Обхоим все инструкции и меняем страый id-шник на новый
            for (int i = 0; i < classGen.getMethods().length; i++) {
                MethodGen methodGen = new MethodGen(classGen.getMethodAt(i), classGen.getClassName(), classGen.getConstantPool());
                InstructionHandle handle = methodGen.getInstructionList().getStart();

                do {
                    Instruction instr = handle.getInstruction();
                    if (instr instanceof CPInstruction) {
                        CPInstruction cpInstruction = (CPInstruction) instr;
                        if (cpInstruction.getIndex() == old_id) {
                            cpInstruction.setIndex(new_id);
                        }
                    }
                    handle = handle.getNext();
                } while (handle != null);
                changes.add(methodGen.getMethod());
            }

            // Вставляем обновлённые методы обратно и радуемся
            classGen.setMethods(changes.toArray(new Method[changes.size()]));

            // Update bytes in Core
            Core.INSTANCE.getClassMap().put(classGen.getClassName(), classGen.getJavaClass().getBytes());
        }
    }

    // Сохранение констант из текстового редактора
    public void saveConstantsFromEditor() {
        String code = cpEditor.getText();
        oClass.setConstantPool(ConstantPool.parseCode(code));

        // Update bytes in Core
        try {
            Core.INSTANCE.getClassMap().put(classGen.getClassName(), oClass.dump());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Приватный класс для представление в таблице
    class EditingCell extends TableCell<ConstantTableCell, String> {

        private TextField textField;

        public EditingCell() {
        }

        @Override
        public void startEdit() {
            super.startEdit();

/*        if (textField == null) {*/
            createTextField();
  /*      }*/

            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(String.valueOf(getItem()));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setGraphic(textField);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                } else {
                    setText(getString());
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        commitEdit(textField.getText());
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }
}

