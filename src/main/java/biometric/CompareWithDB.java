package biometric;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.bioauth.bioauth.User.model.Biometric;
import com.bioauth.bioauth.User.model.User;
import com.bioauth.bioauth.User.repo.BioRepo;
import com.bioauth.bioauth.config.SpringContext;
import com.digitalpersona.uareu.*;

public class CompareWithDB extends JPanel implements ActionListener {
	private BioRepo bioRepo() {
		return SpringContext.getBean(BioRepo.class);
	}

	private static final long serialVersionUID = 6;
	private static final String ACT_BACK = "back";

	private CaptureThread m_capture;
	private Reader m_reader;
	private Fmd newFmd;
	private JDialog m_dlgParent;
	private JTextArea m_text;

	private final String m_strPrompt1 = "Verification started\n    put registered finger on the reader\n\n";

	private CompareWithDB(Reader reader) {
		m_reader = reader;

		final int vgap = 5;
		final int width = 380;

		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);

		m_text = new JTextArea(22, 1);
		m_text.setEditable(false);
		JScrollPane paneReader = new JScrollPane(m_text);
		add(paneReader);
		Dimension dm = paneReader.getPreferredSize();
		dm.width = width;
		paneReader.setPreferredSize(dm);

		add(Box.createVerticalStrut(vgap));

		JButton btnBack = new JButton("Back");
		btnBack.setActionCommand(ACT_BACK);
		btnBack.addActionListener(this);
		add(btnBack);
		add(Box.createVerticalStrut(vgap));

		setOpaque(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(ACT_BACK)) {
			// cancel capture
			StopCaptureThread();
		} else if (e.getActionCommand().equals(CaptureThread.ACT_CAPTURE)) {
			// process result
			CaptureThread.CaptureEvent evt = (CaptureThread.CaptureEvent) e;
			try {
				if (ProcessCaptureResult(evt)) {
					// restart capture thread
					WaitForCaptureThread();
					StartCaptureThread();
				} else {
					// destroy dialog
					m_dlgParent.setVisible(false);
				}
			} catch (Exception e1) {
				try {
					throw new Exception(e1.getMessage());
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		}
	}

	private void StartCaptureThread() {
		m_capture = new CaptureThread(m_reader, false, Fid.Format.ANSI_381_2004,
				Reader.ImageProcessing.IMG_PROC_DEFAULT);
		m_capture.start(this);
	}

	private void StopCaptureThread() {
		if (null != m_capture)
			m_capture.cancel();
	}

	private void WaitForCaptureThread() {
		if (null != m_capture)
			m_capture.join(1000);
	}

	byte[] userFmd = null;
	private void FetchFmdForUser(User user) throws Exception {
		Biometric bio = bioRepo().getUserBiometric(user.getId());
		if (bio != null) {
			userFmd = bio.getBiometric();
		} else {
			throw new Exception("User has no registered biometric");
		}
		
	}

	private boolean ProcessCaptureResult(CaptureThread.CaptureEvent evt) throws Exception {
		boolean bCanceled = false;
		if (null != evt.capture_result) {
			if (null != evt.capture_result.image && Reader.CaptureQuality.GOOD == evt.capture_result.quality) {
				// extract features
				Engine engine = UareUGlobal.GetEngine();

				try {
					Fmd fmd = engine.CreateFmd(evt.capture_result.image, Fmd.Format.ANSI_378_2004);
					newFmd = fmd;
				} catch (UareUException e) {
					MessageBox.DpError("Engine.CreateFmd()", e);
				}

				if (null != newFmd) {
					// perform comparison
					try {
						Fmd dbFmd = UareUGlobal.GetImporter().ImportFmd(userFmd, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
						int falsematch_rate = engine.Compare(dbFmd, 0, newFmd, 0);

						int target_falsematch_rate = Engine.PROBABILITY_ONE / 100000; // target rate is 0.00001
						if (falsematch_rate < target_falsematch_rate) {
							m_text.append("Fingerprints matched.\n");
							String str = String.format("dissimilarity score: 0x%x.\n", falsematch_rate);
							m_text.append(str);
							str = String.format("false match rate: %e.\n\n\n",
									(double) (falsematch_rate / Engine.PROBABILITY_ONE));
							m_text.append(str);
							StopCaptureThread();
							m_dlgParent.setVisible(false);
						} else {
							m_text.append("Fingerprints did not match.\n\n\n");
							StopCaptureThread();
							m_dlgParent.setVisible(false);
							throw new Exception("Fingerprints did not match, Try again!");
						}
					} catch (UareUException e) {
						MessageBox.DpError("Engine.CreateFmd()", e);
					}

					// the new loop starts
				} else {
					// the loop continues
					m_text.append(m_strPrompt1);
				}
			} else if (Reader.CaptureQuality.CANCELED == evt.capture_result.quality) {
				// capture or streaming was canceled, just quit
				bCanceled = true;
			} else {
				// bad quality
				MessageBox.BadQuality(evt.capture_result.quality);
			}
		} else if (null != evt.exception) {
			// exception during capture
			MessageBox.DpError("Capture", evt.exception);
			bCanceled = true;
		} else if (null != evt.reader_status) {
			// reader failure
			MessageBox.BadStatus(evt.reader_status);
			bCanceled = true;
		}

		return !bCanceled;
	}

	private void doModal(JDialog dlgParent) {
		// open reader
		try {
			m_reader.Open(Reader.Priority.COOPERATIVE);
		} catch (UareUException e) {
			MessageBox.DpError("Reader.Open()", e);
		}

		// start capture thread
		StartCaptureThread();

		// put initial prompt on the screen
		m_text.append(m_strPrompt1);

		// bring up modal dialog
		m_dlgParent = dlgParent;
		m_dlgParent.setContentPane(this);
		m_dlgParent.pack();
		m_dlgParent.setLocationRelativeTo(null);
		m_dlgParent.toFront();
		m_dlgParent.setVisible(true);
		m_dlgParent.dispose();

		// cancel capture
		StopCaptureThread();

		// wait for capture thread to finish
		WaitForCaptureThread();

		// close reader
		try {
			m_reader.Close();
		} catch (UareUException e) {
			MessageBox.DpError("Reader.Close()", e);
		}
	}

	public static void Run(Reader reader, User user) throws Exception {
		JDialog dlg = new JDialog((JDialog) null, "Verification", true);
		CompareWithDB verification = new CompareWithDB(reader);
		verification.FetchFmdForUser(user);
		verification.doModal(dlg);
	}
}
