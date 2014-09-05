package com.thedeanda.ajaxproxy.ui;

import java.awt.Insets;

import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class SwingUtils {
	public static JTextField newJTextField() {
		return prepJTextField(new JTextField());
	}

	public static JTextField prepJTextField(JTextField field) {
		Insets insets = new Insets(4, 4, 4, 4);
		field.setMargin(insets);
		return field;
	}

	public static void flattenSplitPane(JSplitPane jSplitPane) {
		// *
		jSplitPane.setUI(new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					private static final long serialVersionUID = 1L;

					public void setBorder(Border b) {
					}
				};
			}
		});
		jSplitPane.setBorder(null);
		// */
	}
}
