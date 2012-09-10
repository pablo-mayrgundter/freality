package os.desktop;

import java.awt.event.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import os.util.Debug;

public class Utils {

    static final String PASSWORD_PROMPT_TEXT = getRsrcProp("desktop.passwordPrompt");
    static final String PASSWORD_PROMPT_TEXT_MNEMONIC = "p";

    static JButton makeSimpleButton(final String name, int width, int height,
                                    final Object callee, final String callbackMethodName, boolean enabled) {
        final JButton navButton = new JButton(name);
        navButton.setSize(width, height);
        navButton.setEnabled(enabled);
        navButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand().equals(name)) {
                        try {
                            final Method callback = callee.getClass().getDeclaredMethod(callbackMethodName);
                            callback.invoke(callee);
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }}});
        return navButton;
    }

    static String readAll(InputStream is) {
        try {
            final BufferedReader r = new BufferedReader(new InputStreamReader(is));
            final StringBuffer buf = new StringBuffer();
            String line;
            while ((line = r.readLine()) != null) { // Should read by byte [] instead.
                buf.append(line);
                buf.append('\n');
            }
            return buf.toString();
        } catch (IOException e) {
            assert Debug.trace(e);
            return null;
        }
    }

    java.io.File promptFile() {
        return null;
    }

    /*
      String promptPasswd(PasswordVerifier verifier) {
      final JPasswordField field = new JPasswordField();
      field.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      if (e.)
      }
      });
      final JLabel label = new JLabel(PASSWORD_PROMPT_TEXT);
      label.setLabelFor(field);
      label.setDisplayedMnemonic(PASSWORD_PROMPT_TEXT_MNEMONIC);
      final JInternalFrame popup = new JInternalFrame();
      popup.add(label);
      popup.add(field);
      popup.show();
      return new String(field.getPassword());
      return "";
      }*/

    // May phase this out as resources become more numerous and default makes less sense.
    public static String getRsrcProp(String propertyName) {
        final ResourceBundle resources = ResourceBundle.getBundle("os/desktop/DefaultResources",
                                                                  Locale.getDefault());
        if (resources != null) {
            return resources.getString(propertyName);
        }
        return null;
    }

    /** @return The requested String or null if not found. */
    public static String getRsrcProp(String bundleName, String propertyName) {
        final ResourceBundle resources = ResourceBundle.getBundle(bundleName, Locale.getDefault());
        if (resources != null) {
            return resources.getString(propertyName);
        }
        return null;
    }

    public static String getUserPref(String name) {
        return null;
    }
}
