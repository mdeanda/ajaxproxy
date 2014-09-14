package com.thedeanda.ajaxproxy.ui;

import java.awt.Insets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class SwingUtils {
	private static final Executor executor = Executors.newFixedThreadPool(3);

	public static JLabel newJLabel(String label) {
		JLabel ret = new JLabel(label);
		ret.setHorizontalAlignment(SwingConstants.RIGHT);
		return ret;
	}

	public static JTextField newJTextField() {
		return prepJTextField(new JTextField());
	}

	public static JTextField prepJTextField(JTextField field) {
		Insets insets = new Insets(4, 4, 4, 4);
		field.setMargin(insets);
		return field;
	}

	public static JTextArea newJTextArea() {
		JTextArea ret = new JTextArea();
		Insets insets = new Insets(4, 4, 4, 4);
		ret.setMargin(insets);
		// ret.setHorizontalAlignment(SwingConstants.RIGHT);
		return ret;
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

	public static void executNonUi(Runnable runnable) {
		executor.execute(runnable);
		;
	}
}
