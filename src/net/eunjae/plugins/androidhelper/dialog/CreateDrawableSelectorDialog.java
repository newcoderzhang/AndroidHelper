package net.eunjae.plugins.androidhelper.dialog;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class CreateDrawableSelectorDialog extends JDialog {
    private Project project;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField selectorNameTextField;
    private JTextField fileNameTextField;
    private JCheckBox disabledResIDCheckBox;
    private JCheckBox pressedResIDCheckBox;
    private JCheckBox normalResIDCheckBox;
    private JTextField disabledTextField;
    private JTextField pressedTextField;
    private JTextField normalTextField;
    private String projectPath = null;

    public CreateDrawableSelectorDialog(Project project) {
        this.projectPath = project.getBasePath();
        this.project = project;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        selectorNameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateEveryName();
            }
        });

        addCheckBoxListener(disabledResIDCheckBox, disabledTextField);
        addCheckBoxListener(pressedResIDCheckBox, pressedTextField);
        addCheckBoxListener(normalResIDCheckBox, normalTextField);
    }

    private void updateEveryName() {
        String name = selectorNameTextField.getText();
        fileNameTextField.setText(String.format("/res/drawable/%s.xml", name));

        updateResName(disabledResIDCheckBox, disabledTextField, name, "_disabled");
        updateResName(pressedResIDCheckBox, pressedTextField, name, "_pressed");
        updateResName(normalResIDCheckBox, normalTextField, name, "_normal");
    }

    private void addCheckBoxListener(final JCheckBox checkBox, final JTextField textField) {
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField.setEnabled(checkBox.isSelected());
                updateEveryName();
            }
        });
    }

    private void updateResName(JCheckBox checkBox, JTextField textField, String name, String postfix) {
        if (checkBox.isSelected()) {
            textField.setText(name + postfix);
        } else {
            textField.setText("");
        }
    }

    private void onOK() {
        dispose();
        String content = buildContent();
        File file = exportToFile(content);
        openOnEditor(file);
    }

    private void openOnEditor(File file) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
        FileEditorManager.getInstance(project).openFile(virtualFile, true);
    }

    private String buildContent() {
        String content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<selector xmlns:android=\"http://schemas.android.com/apk/res/android\">\n";
        if (disabledResIDCheckBox.isSelected()) {
            content += "    <item android:drawable=\"@drawable/" + disabledTextField.getText() + "\" android:state_enabled=\"false\"/>\n";
        }
        if (pressedResIDCheckBox.isSelected()) {
            content += "    <item android:drawable=\"@drawable/" + pressedTextField.getText() + "\" android:state_pressed=\"true\"/>\n";
        }
        if (normalResIDCheckBox.isSelected()) {
            content += "    <item android:drawable=\"@drawable/" + normalTextField.getText() + "\"/>\n";
        }
        content +=
                "</selector>";
        return content;
    }

    private File exportToFile(String content) {
        File file = new File(projectPath + "/" + fileNameTextField.getText());
        File dir = file.getParentFile();
        if (dir.exists() == false) {
            dir.mkdirs();
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(content);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return file;
    }

    private void onCancel() {
        dispose();
    }
}
