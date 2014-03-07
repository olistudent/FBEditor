package de.FBEditor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.FBEditor.struct.JTextPane2;

/**
 * Search & Replace window
 *
 */
public class FindReplace extends JDialog {

	public FindReplace(FBEdit parent, int type) {
		super(parent, type != 2 ? "Suchen" : "Suchen und Ersetzen");
		this.type = type;
		initComponents();
		setLocationRelativeTo(parent);
		setVisible(true);
		pane = parent.getJTextPane();
	}

	private void initComponents() {
		JPanel jPanel1 = new JPanel();
		JPanel jPanel2 = new JPanel();
		JPanel jPanel3 = new JPanel();
		JPanel jPanel4 = new JPanel();
		JPanel jPanel5 = new JPanel();
		JPanel jPanel6 = new JPanel();
		
		JLabel jLabel1 = new JLabel();
		JLabel jLabel2 = new JLabel();

		JButton jButton1 = new JButton();
		JButton jButton4 = new JButton();
		JButton jButton2 = new JButton();
		@SuppressWarnings("unused")
		JButton jButton3 = new JButton();
		JButton jButton5 = new JButton();
		
		TFfind = new JTextField();
		TFreplace = new JTextField();
		/*
		final CutAndPaste CAP = new CutAndPaste();
		final JPopupMenu popup = CAP.getPopupMenu();
		
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent evt) {
				closeDialog(evt);
			}
		});
		
		MouseAdapter mouseadapt = new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					CAP.updateSource((JTextComponent) e.getSource());
					CAP.updateMenu();
					popup.show((Component) e.getSource(), e.getX(), e.getY());
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					CAP.updateSource((JTextComponent) e.getSource());
					CAP.updateMenu();
					popup.show((Component) e.getSource(), e.getX(), e.getY());
				}
			}
		};
		
		ActionListener myListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButton1ActionPerformed();
			}
		};

		TFfind.addActionListener(myListener);
		TFreplace.addActionListener(myListener);

		TFreplace.addMouseListener(mouseadapt);
		TFfind.addMouseListener(mouseadapt);
*/
		jPanel1.setLayout(new BoxLayout(jPanel1, 1));
		jPanel3.setPreferredSize(new Dimension(20, 20));
		jLabel1.setText("Suchen");
		jPanel3.add(jLabel1);
		jPanel1.add(jPanel3);
		jPanel4.setLayout(new FlowLayout(0));
		TFfind.setPreferredSize(new Dimension(150, 20));
		jPanel4.add(TFfind);
		jPanel1.add(jPanel4);
		jPanel5.setPreferredSize(new Dimension(20, 20));
		jLabel2.setText("Ersetzen");
		if (type != 2)
			jLabel2.setEnabled(false);
		jPanel5.add(jLabel2);
		jPanel1.add(jPanel5);
		jPanel6.setLayout(new FlowLayout(0));
		TFreplace.setPreferredSize(new Dimension(150, 20));
		if (type != 2)
			TFreplace.setEnabled(false);
		jPanel6.add(TFreplace);
		jPanel1.add(jPanel6);
		getContentPane().add(jPanel1, "Center");
		jPanel2.setLayout(new BoxLayout(jPanel2, 1));
		jButton1.setText("Weitersuchen");
		jButton1.setMaximumSize(new Dimension(115, 26));
		jButton1.setPreferredSize(new Dimension(115, 26));
		jButton1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				findActionPerformed();
			}
		});
		jPanel2.add(jButton1);
		jButton4.setText("Alle Suchen");
		jButton4.setMaximumSize(new Dimension(115, 26));
		jButton4.setPreferredSize(new Dimension(115, 26));
		jButton4.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				findAllActionPerformed();
			}
		});
		jPanel2.add(jButton4);
		jButton2.setText("Ersetzen");
		jButton2.setMaximumSize(new Dimension(115, 26));
		jButton2.setPreferredSize(new Dimension(115, 26));
		jButton2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				replaceActionPerformed();
			}
		});
		if (type != 2)
			jButton2.setEnabled(false);
		jPanel2.add(jButton2);
		/*
		jButton3.setText("Alle Ersetzen");
		jButton3.setMaximumSize(new Dimension(115, 26));
		jButton3.setPreferredSize(new Dimension(115, 26));
		jButton3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				replaceAllActionPerformed();
			}
		});
		if (type != 2)
			jButton3.setEnabled(false);
		jPanel2.add(jButton3);
		*/
		jButton5.setText("Abbrechen");
		jButton5.setMaximumSize(new Dimension(115, 26));
		jButton5.setPreferredSize(new Dimension(115, 26));
		jButton5.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				jButton5ActionPerformed();
			}
		});
		jPanel2.add(jButton5);
		getContentPane().add(jPanel2, "East");
		pack();
	}

	private void findActionPerformed() {
		JTextPane2.findText(pane, TFfind.getText());
	}

	private void findAllActionPerformed() {
		JTextPane2.findAll(pane, TFfind.getText());
	}

	private void replaceActionPerformed() {
		JTextPane2.replaceText(pane, TFreplace.getText());
	}

	@SuppressWarnings("unused")
	private void replaceAllActionPerformed() {
		JTextPane2.replaceAll(pane, TFreplace.getText());
	}

	private void jButton5ActionPerformed() {
		setVisible(false);
		dispose();
	}
/*
	private void closeDialog(WindowEvent evt) {
		setVisible(false);
		dispose();
	}
*/
	/* continue search on F3 */
	public void searchon() {
		findActionPerformed();
	}

	public static final int SEARCH = 1;
	public static final int REPLACE = 2;
	private int type;
	private static final long serialVersionUID = 1L;
	private JTextField TFreplace;
	private JTextField TFfind;
	private JTextPane2 pane;

}
