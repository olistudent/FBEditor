package de.FBEditor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.FBEditor.exceptions.InvalidFirmwareException;
import de.FBEditor.exceptions.WrongPasswordException;

public class BoxInfo extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jPanel = null;
	private JButton jButton = null;
	private JScrollPane jScrollPane = null;
	private JEditorPane fbeditorPane = null;
	private JPanel jPanel1 = null;
	private JLabel jLabel2;
	private JLabel jLabel4;
	private JLabel jLabel6;
	
	/**
	 * This is the default constructor
	 * @throws InvalidFirmwareException 
	 * @throws IOException 
	 * @throws WrongPasswordException 
	 */
	public BoxInfo(String boxName, String firmwareVersion, String modVersion) {
		super();
		
		this.setSize(500, 300);
		this.setTitle(FBEdit.getMessage("menu.boxinfo"));
		this.setIconImage(FBEdit.getInstance().getImageFromJAR("/icon.gif"));
		this.setContentPane(getJPanel());
		this.setLocationRelativeTo(FBEdit.getInstance().getframe());
		this.setVisible(true);
		
		setPanelText(boxName, firmwareVersion, modVersion);
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			// gridBagConstraints2.insets = new java.awt.Insets(53,3,112,74);
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 0.25;
			gridBagConstraints2.fill = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			// gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 0.65;
			gridBagConstraints1.fill = 1;
			// gridBagConstraints1.insets = new java.awt.Insets(5,3,64,2);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			// gridBagConstraints.insets = new java.awt.Insets(100,74,159,2);
			gridBagConstraints.gridy = 2;
			gridBagConstraints.gridx = 0;
			// gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 0.1;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			jPanel.add(getJButton(), gridBagConstraints);
			jPanel.add(getJScrollPane(), gridBagConstraints1);
			jPanel.add(getJPanel1(), gridBagConstraints2);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setPreferredSize(new java.awt.Dimension(115, 35));
			jButton.setText(FBEdit.getMessage("menu.close"));
			jButton.setToolTipText(FBEdit.getMessage("window.close"));
			jButton.setName("close");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new java.awt.Dimension(200, 200));
			jScrollPane.setViewportView(getJEditorPane());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes fbeditorPane
	 * 
	 * @return javax.swing.fbeditorPane
	 */
	private JEditorPane getJEditorPane() {
		if (fbeditorPane == null) {
			try {
				String url = (new StringBuilder("http://")).append(FBEdit.getInstance().getbox_address()).append("/cgi-bin/system_status").toString();
				fbeditorPane = new JEditorPane(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fbeditorPane;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridLayout(4, 2));
			JLabel jLabel = new JLabel(FBEdit.getMessage("boxinfo.hardware"));
			jLabel.setPreferredSize(new Dimension(100, 26));
			jPanel1.add(jLabel);
			jLabel2 = new JLabel();
			jLabel2.setPreferredSize(new Dimension(100, 26));
			jPanel1.add(jLabel2);
			jLabel = new JLabel(FBEdit.getMessage("boxinfo.firmware"));
			jLabel.setPreferredSize(new Dimension(100, 26));
			jPanel1.add(jLabel);
			jLabel4 = new JLabel();
			jLabel4.setPreferredSize(new Dimension(100, 26));
			jPanel1.add(jLabel4);
			jLabel = new JLabel(FBEdit.getMessage("boxinfo.modversion"));
			jLabel.setPreferredSize(new Dimension(100, 26));
			jPanel1.add(jLabel);
			jLabel6 = new JLabel();
			jLabel6.setPreferredSize(new Dimension(100, 26));
			jPanel1.add(jLabel6);
		}
		return jPanel1;
	}

	public void setPanelText(String boxName, String firmwareVersion, String modVersion) {
		jLabel2.setText(boxName);
		jLabel4.setText(firmwareVersion);
		/*
		if (modVersion.equals(""))
			jLabel6.setText(FBEdit.getMessage("boxinfo.nomod"));
		else
			jLabel6.setText(modVersion);
		*/
		if (modVersion.equals("")) {
			jLabel6.setText(FBEdit.getMessage("boxinfo.nomod"));
		} else {
			jLabel6.setText(modVersion);
		}
	}
}
