package com.thedeanda.ajaxproxy.ui.util;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import com.thedeanda.ajaxproxy.ui.util.CaretMovingKeyListener;
import org.apache.commons.lang3.StringUtils;

public class SwingUtils {
	private static final ExecutorService executor = Executors.newFixedThreadPool(10);

	public static JLabel newJLabel(String label) {
		JLabel ret = new JLabel(label);
		ret.setHorizontalAlignment(SwingConstants.RIGHT);
		return ret;
	}

	public static JToggleButton newJToggleButton(String label) {
		JToggleButton btn = new JToggleButton(label);
		//btn.setMargin(new Insets(2, 10, 2, 10));
		return btn;
	}

	public static JButton newJButton(String label) {
		JButton btn = new JButton(label);
		btn.setMargin(new Insets(2, 20, 2, 20));
		return btn;
	}
	
	public static JComboBox<?> newJComboBox(Object[] items) {
		JComboBox<Object> combo = new JComboBox<>(items);
		// Insets insets = new Insets(4, 4, 4, 4);
		combo.setBorder(BorderFactory.createEmptyBorder());

		return combo;
	}

	/** adds regular expression support */
	public static JTextField newJTextReField(ReFieldChangeListener listener) {
		final JTextField tf = newJTextField();

		final Color filterOkColor = tf.getBackground();
		final Color filterBadColor = new Color(250, 210, 200);

		tf.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                resetFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                resetFilter();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                resetFilter();
            }

            private void resetFilter() {
                String txt = tf.getText();
                if (!StringUtils.isBlank(txt)) {
                    Pattern regEx;
                    try {
                        regEx = Pattern.compile(txt);
                        tf.setBackground(filterOkColor);
                    } catch (PatternSyntaxException ex) {
                        regEx = null;
                        tf.setBackground(filterBadColor);
                    }
                    listener.fieldChanged(regEx);
                }
            }
        });

		return tf;
	}

	public static JTextField newJTextField() {
		return prepJTextField(new JTextField());
	}

	public static JTextField prepJTextField(final JTextField field) {
		Insets insets = new Insets(4, 4, 4, 4);
		field.setMargin(insets);

		field.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				field.select(0, field.getText().length());
			}

			@Override
			public void focusLost(FocusEvent e) {
				field.select(0, 0);
			}
		});

		field.addKeyListener(new CaretMovingKeyListener(field));

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
		UIDefaults defaults = javax.swing.UIManager.getDefaults();
		final Color light = defaults.getColor("SplitPane.highlight");
		final Color dark = defaults.getColor("SplitPane.darkShadow");

		// *
		jSplitPane.setUI(new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				BasicSplitPaneDivider divider = new BasicSplitPaneDivider(this) {
					private static final long serialVersionUID = 1L;

					@Override
					public int getDividerSize() {
						return 5;
					}

					@Override
					public void paint(Graphics g) {
						// super.paint(g);
						int orientation = this.getBasicSplitPaneUI().getOrientation();

						Dimension size = this.getSize();

						if (orientation == JSplitPane.VERTICAL_SPLIT) {
							int[] lines = new int[2];
							lines[0] = 0;
							lines[1] = size.height - 2;

							for (int i = 0; i < size.width; i += 4) {
								for (int j = 0; j < lines.length; j++) {
									int y = lines[j];
									g.setColor(light);
									g.fillRect(i, y, 2, 2);
									g.setColor(dark);
									g.fillRect(i, y, 1, 1);
								}
							}
						} else {
							int[] rows = new int[2];
							rows[0] = 0;
							rows[1] = size.width - 2;

							for (int i = 0; i < size.height; i += 4) {
								for (int j = 0; j < rows.length; j++) {
									int x = rows[j];
									g.setColor(light);
									g.fillRect(x, i, 2, 2);
									g.setColor(dark);
									g.fillRect(x, i, 1, 1);
								}
							}
						}
					}
				};
				return divider;
			}
		});
		jSplitPane.setBorder(null);
		// */
	}

	public static void executNonUi(Runnable runnable) {
		executor.execute(runnable);
	}
}
