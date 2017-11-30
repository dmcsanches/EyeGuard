
import java.awt.EventQueue;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.Color;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.JSlider;

import org.irisid.camera.ITD100Events;
import org.irisid.camera.TD100;
import org.irisid.camera.TD100Constants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import java.awt.image.RenderedImage;
import java.awt.Font;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.border.BevelBorder;
import javax.swing.SwingUtilities;
import java.util.UUID;

public class MainFrame extends JFrame implements ITD100Events, ActionListener
{	
	// Image Formats
    public static final int IMG_FMT_RAW = 0;
    public static final int IMG_FMT_PNG = 1;
    public static final int IMG_FMT_JPEG = 2;
    public static final int IMG_FMT_JPEG2000 = 3;
    public static final int IMG_FMT_ISO = 4;

    // Face Guide Box Limits
    // CMD_SET_FACE_BOX_OUTLINE

    public static final int MIN_FACE_BOX_OUT_X = 96;
    public static final int MAX_FACE_BOX_OUT_X = 1280;
    public static final int MIN_FACE_BOX_OUT_Y = 96;
    public static final int MAX_FACE_BOX_OUT_Y = 960; 

    // CMD_SET_FACE_BOX_INLNE

    public static final int MIN_FACE_BOX_IN_X = 96;
    public static final int MAX_FACE_BOX_IN_X = 1280;
    public static final int MIN_FACE_BOX_IN_Y = 96;
    public static final int MAX_FACE_BOX_IN_Y = 960;

    public static final int MIN_FACE_BOX_IN_WIDTH = 48;
    public static final int MAX_FACE_BOX_IN_WIDTH = 1232;
    public static final int MIN_FACE_BOX_IN_HEIGHT = 48;
    public static final int MAX_FACE_BOX_IN_HEIGHT = 912;
    
    //Default Face Guide Box Values

    public static final long innerWidthVal = 360;
    public static final long innerHeightVal = 520;
    public static final long outerWidthval = 480;
    public static final long outerHeightVal = 640;
    
    //Constant Values
    
    public static final int FACE_BOX_NONE= 0;
    public static final int FACE_BOX_DISPLAY= 1;
	public static final String IMAGE_PATH = "images/App.png";
	public static final String FACE_FOLDER = "Face";
	public static final String SCENE_FOLDER = "Scene";
	public static final String RECOGNITION_FOLDER = "res";
	public static final String ENROLLMENT_FOLDER = "Fotos Olhos";
	
	// Language
    public static final int LNG_NONE = 0;
    public static final int LNG_KOREAN = 1;
    public static final int LNG_ENGLISH = 2;
    public static final int LNG_ARABIC = 3;
    public static final int LNG_CHINESE_MANDARIN = 4;
    public static final int LNG_CHINESE_CANTONESE = 5;

    static TD100 jtd100 = new TD100();
    BMPFile bmpFile = new BMPFile();
    StringBuffer sbSerialNumber = null;
    StringBuffer versionNumber = null;
    Component components[] = null;
    JButton btnSaveSelectedImages;
    JCheckBox chckbxSaveIrisImages;
    
    private JPanel contentPaneIrisSampleApp, panelLiveImages;
    private JTextField txtSerialNumber, txtVersion, txtFieldInnerWidth;
    private JTextField txtFieldInnerHeight, txtFieldOuterWidth, txtFieldOuterHeight;
    private JButton btnSleep, btnConnect, btnDisconnect, btnGetVersion;
    private JLabel lblSerialNumber, lblRange, lblVelocity, lblDistance, lblRightIris, lblLeftIris, lblRangeValue, lblVelocityValue, lblDistanceValue;
    private GroupLayout gl_panelLive, gl_panelCapturedImages;
    private JPanel panelRightIris, panelLeftIris, panelCamera,  panelImageCapture;
    private JPanel panelIris, panelCameraControl;
    private JComboBox comboBoxLanguage, comboBoxSound, comboBoxLED, cmbVersion, comboBoxEyeType;
    private JButton btnSetLanguage, btnPlaySound, btnSetLED;
    private JCheckBox chckbxShowLiveImages, chckbxShowFaceBox;
    private JLabel lblInnerWidth, lblInnerHeight, lbOuterWidth, lblOuterHeight, lblLiveImage, lblVersion;
    private JLabel lblFaceCaptureImage, lblSceneCapture, lblRightIrisCapture, lblLeftIrisCapture;
    private JRadioButton rdbtnEnrollment, rdbtnRecognition;
    private JButton btnTakePicture, btnCaptureScene, btnTakeScene, btnCaptureFace, btnCancel, btnCaptureIris, btnWakeup;
    private ButtonGroup bgroup;
    private JSlider sliderVolume;
    long openResult = -1;
    long irisResult = -1;
    int[] numberOfLanguages = { 0 };
    short[] arrayOfLanguageIDs = new short[12];
    String[] listOfLanguages = { 
		        "NONE       ",
                "KOREAN     ",
                "ENGLISH    ",
                "ARABIC     ",
                "MANDARIN   ",
                "CANTONESE  ",
                "CZECH      ",
                "DANISH     ",
                "DUTCH      ",
                "FINNISH    ",
                "FRENCH     ",
                "GERMAN     ",
                "GREEK      ",
                "HEBREW     ",
                "HINDI      ",
                "HUNGARIAN  ",
                "ITALIAN    ",
                "JAPANESS   ",
                "BOKMAL     ",
                "NYNORSK    ",
                "POLISH     ",
                "PORTUGUESE ",
                "RUSSIAN    ",
                "SLOVAK     ",
                "SPANISH    ",
                "SWEDISH    ",
                "TURKISH    " 
	};

    int languageIndex = 0;
    long inputWidthValue = innerWidthVal;
    long inputHeightValue = innerHeightVal;
    long outputWidthValue = outerWidthval;
    long outputHeightValue = outerHeightVal;
    String actionCommand;
    boolean toggle, toggle_face = false;
    boolean faceCaptureInProgress = false;
    boolean faceCreated = false;
    boolean sceneCreated = false;
    boolean recognitionCreated, enrollmentCreated = false;
    boolean enrollmentMode, recognitionMode = false;
    boolean icamCreated = false;
    String folderStructure;
    JCheckBox chckbxSaveFaceImage;
    JCheckBox chckbxSaveSceneImage;
    String userHome, facePath, scenePath, irisRecognitionPath, irisEnrollmentPath;
    byte[] rightRawImageSave;
    byte[] leftRawImageSave;
    byte[] faceBMP;
    byte[] sceneBMP;
    long tempinputWidthValue, tempinputHeightValue, tempoutputWidthvalue, tempoutputHeightValue;

    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
	EventQueue.invokeLater(new Runnable()
	{
	    public void run()
	    {
		try
		{
		    /*
		     * The Point gives the co-ordinates of the Center of the
		     * Screen excluding the taskbar. The UI Launches from the
		     * points calculated below.
		     */

			MainFrame frame = new MainFrame();
		    Point screenCenter = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		    frame.setLocation(screenCenter.x - (frame.getWidth() / 2), screenCenter.y - (frame.getHeight() / 2));
		    frame.setVisible(true);
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the frame with the appropriate co-ordinates and the width and
     * height.
     * 
     * @throws IOException
     * @throws IOException
     */
    public MainFrame() throws IOException
    {
	//jtd100.SetCallback(this);
	setBackground(UIManager.getColor("ComboBox.buttonBackground"));
	ClassLoader cldr = this.getClass().getClassLoader();
	java.net.URL imageURL   = cldr.getResource(IMAGE_PATH);

	WindowListener listener = new WindowAdapter()
	{
	    public void windowClosing(WindowEvent w)
	    {
	        Disconnect(true);
	      }
	    };
	addWindowListener(listener);

	// setting the version number on the label of the frame
	versionNumber = new StringBuffer();
    jtd100.GetVersion(TD100Constants.VERSION_SDK, versionNumber);
    System.out.println("Version: " + versionNumber);
    String titleVersion = versionNumber.toString();
	
    setTitle("iCAM TD100 Control v" + titleVersion);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setSize(1029, 733);

	contentPaneIrisSampleApp = new JPanel();
	contentPaneIrisSampleApp.setBackground(UIManager.getColor("ComboBox.buttonBackground"));
	contentPaneIrisSampleApp.setBorder(new EmptyBorder(0, 0, 0, 0));
	setContentPane(contentPaneIrisSampleApp);
	setResizable(false);

	panelCamera = new JPanel();
	panelCamera.setLocation(29, 2);
	Font font = new Font("SanSerif", Font.PLAIN, 9);
	panelCamera.setBorder(new TitledBorder(null, "Camera", TitledBorder.LEADING, TitledBorder.TOP, font, Color.BLUE));

	panelImageCapture = new JPanel();
	panelImageCapture.setBorder(new TitledBorder(null, "Image Capture", TitledBorder.LEADING, TitledBorder.TOP, font, Color.BLUE));

	panelCameraControl = new JPanel();
	panelCameraControl.setBorder(new TitledBorder(null, "Camera Control", TitledBorder.LEADING, TitledBorder.TOP, font, Color.BLUE));

	JPanel panelCapturedImages = new JPanel();
	panelCapturedImages.setBorder(new TitledBorder(null, "Captured Images", TitledBorder.LEADING, TitledBorder.TOP, font, Color.BLUE));
	GroupLayout gl_contentPaneIrisSampleApp = new GroupLayout(contentPaneIrisSampleApp);
	gl_contentPaneIrisSampleApp.setHorizontalGroup(gl_contentPaneIrisSampleApp.createParallelGroup(Alignment.LEADING).addGroup(
		gl_contentPaneIrisSampleApp.createSequentialGroup().addGroup(
			gl_contentPaneIrisSampleApp.createParallelGroup(Alignment.LEADING).addGroup(
				gl_contentPaneIrisSampleApp.createSequentialGroup().addGap(10).addGroup(
					gl_contentPaneIrisSampleApp.createParallelGroup(Alignment.LEADING).addComponent(panelImageCapture, 0, 0, Short.MAX_VALUE).addComponent(panelCamera,
						Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))).addGroup(
				gl_contentPaneIrisSampleApp.createSequentialGroup().addContainerGap().addComponent(panelCameraControl, GroupLayout.PREFERRED_SIZE, 384, Short.MAX_VALUE)))
			.addPreferredGap(ComponentPlacement.RELATED).addComponent(panelCapturedImages, GroupLayout.PREFERRED_SIZE, 613, GroupLayout.PREFERRED_SIZE).addContainerGap()));
	gl_contentPaneIrisSampleApp.setVerticalGroup(gl_contentPaneIrisSampleApp.createParallelGroup(Alignment.TRAILING).addGroup(
		gl_contentPaneIrisSampleApp.createSequentialGroup().addComponent(panelCamera, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE).addGap(0).addComponent(panelImageCapture, GroupLayout.PREFERRED_SIZE, 215,
			//GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(panelCameraControl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
			GroupLayout.PREFERRED_SIZE).addGap(0).addComponent(panelCameraControl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
			GroupLayout.PREFERRED_SIZE).addGap(25)).addGroup(
		Alignment.LEADING,
		gl_contentPaneIrisSampleApp.createSequentialGroup().addComponent(panelCapturedImages, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addContainerGap()));

	panelRightIris = new JPanel();
	panelRightIris.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
	panelRightIris.setSize(360, 270);

	panelLeftIris = new JPanel();
	panelLeftIris.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
	panelLeftIris.setSize(360, 270);

	
	
	btnSaveSelectedImages = new JButton("Save");
	btnSaveSelectedImages.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnSaveSelectedImages.setActionCommand("SaveImages");
	btnSaveSelectedImages.addActionListener(this);
	btnSaveSelectedImages.setEnabled(false);
	
	gl_panelCapturedImages = new GroupLayout(panelCapturedImages);
	gl_panelCapturedImages.setHorizontalGroup(gl_panelCapturedImages.createParallelGroup(Alignment.LEADING).addGroup(
		gl_panelCapturedImages.createSequentialGroup().addGap(90)
			).addGroup(
		gl_panelCapturedImages.createSequentialGroup().addGap(20).addComponent(btnSaveSelectedImages, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE).addComponent(panelRightIris, GroupLayout.PREFERRED_SIZE, 268, GroupLayout.PREFERRED_SIZE).addPreferredGap(
			ComponentPlacement.UNRELATED).addComponent(panelLeftIris, GroupLayout.PREFERRED_SIZE, 267, GroupLayout.PREFERRED_SIZE).addContainerGap(32, Short.MAX_VALUE)).addGroup(
		gl_panelCapturedImages.createSequentialGroup().addGap(88).addPreferredGap(ComponentPlacement.RELATED, 304, Short.MAX_VALUE))
		.addGroup(
			gl_panelCapturedImages.createSequentialGroup().addGap(23).addPreferredGap(
				ComponentPlacement.RELATED, 22, Short.MAX_VALUE).addGap(19)).addGroup(
			gl_panelCapturedImages.createSequentialGroup().addGap(59)
				.addContainerGap(137, Short.MAX_VALUE)).addGroup(
			gl_panelCapturedImages.createSequentialGroup().addGap(185).addComponent(btnSaveSelectedImages, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE).addContainerGap(
				259, Short.MAX_VALUE)).addGroup(gl_panelCapturedImages.createSequentialGroup().addGap(237)));


	lblLeftIrisCapture = new JLabel("");
	panelLeftIris.setSize(280, 210);
	lblLeftIrisCapture.setSize(280, 210);
	GroupLayout gl_panelLeftIris = new GroupLayout(panelLeftIris);
	gl_panelLeftIris.setHorizontalGroup(gl_panelLeftIris.createParallelGroup(Alignment.TRAILING)
		.addComponent(lblLeftIrisCapture, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE));
	gl_panelLeftIris.setVerticalGroup(gl_panelLeftIris.createParallelGroup(Alignment.LEADING).addComponent(lblLeftIrisCapture, GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE));
	panelLeftIris.setLayout(gl_panelLeftIris);

	lblRightIrisCapture = new JLabel("");
	panelRightIris.setSize(280, 210);
	lblRightIrisCapture.setSize(280, 210);
	GroupLayout gl_panelRightIris = new GroupLayout(panelRightIris);
	gl_panelRightIris.setHorizontalGroup(gl_panelRightIris.createParallelGroup(Alignment.TRAILING).addComponent(lblRightIrisCapture, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 264,
		Short.MAX_VALUE));
	gl_panelRightIris.setVerticalGroup(gl_panelRightIris.createParallelGroup(Alignment.LEADING).addComponent(lblRightIrisCapture, GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE));
	panelRightIris.setLayout(gl_panelRightIris);

	lblSceneCapture = new JLabel("");
	lblSceneCapture.setPreferredSize(new Dimension(346, 260));
	lblSceneCapture.setMaximumSize(new Dimension(346, 260));
	lblSceneCapture.setMinimumSize(new Dimension(346, 260));
	
	lblFaceCaptureImage = new JLabel("");
	lblFaceCaptureImage.setPreferredSize(new Dimension(195, 260));
	lblFaceCaptureImage.setMaximumSize(new Dimension(195, 260));
	lblFaceCaptureImage.setMinimumSize(new Dimension(195, 260));
	    lblFaceCaptureImage.repaint();

	JLabel lblVolume = new JLabel("Volume:");
	lblVolume.setFont(new Font("SansSerif", Font.PLAIN, 11));

	sliderVolume = new JSlider();
	sliderVolume.setEnabled(false);
	sliderVolume.setValue(5);
	sliderVolume.setMaximum(10);
	sliderVolume.addChangeListener(new ChangeListener()
	{
	    /**
	     * Set's the Volume of the iCAM(Minimum of 0 to Maximum of 10)
	     */
	    public void stateChanged(ChangeEvent arg0)
	    {
		jtd100.SetSoundVolume(sliderVolume.getValue());
		sliderVolume.setValue(sliderVolume.getValue());
	    }
	});

	JLabel lblSound = new JLabel("Sound");
	lblSound.setFont(new Font("SansSerif", Font.PLAIN, 11));

	JLabel lblLED = new JLabel("LED");
	lblLED.setFont(new Font("SansSerif", Font.PLAIN, 11));

	comboBoxSound = new JComboBox();
	comboBoxSound.setFont(new Font("SansSerif", Font.PLAIN, 11));
	comboBoxSound.setEnabled(false);

	comboBoxLED = new JComboBox();
	comboBoxLED.setFont(new Font("SansSerif", Font.PLAIN, 12));
	comboBoxLED.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Success", "Failure", "Busy", "Error", "Turn off" }));
	comboBoxLED.setEnabled(false);

	btnPlaySound = new JButton("Play Sound");
	btnPlaySound.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnPlaySound.setActionCommand("PlaySound");
	btnPlaySound.addActionListener(this);
	btnPlaySound.setEnabled(false);

	btnSetLED = new JButton("Set LED");
	btnSetLED.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnSetLED.addActionListener(this);
	btnSetLED.setActionCommand("SetLED");
	btnSetLED.setEnabled(false);

	JLabel lblLanguage = new JLabel("Language");
	lblLanguage.setFont(new Font("SansSerif", Font.PLAIN, 11));

	btnSetLanguage = new JButton("Set Language");
	btnSetLanguage.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnSetLanguage.addActionListener(this);
	btnSetLanguage.setActionCommand("SetLanguage");
	btnSetLanguage.setEnabled(false);

	comboBoxLanguage = new JComboBox();
	comboBoxLanguage.setFont(new Font("SansSerif", Font.PLAIN, 11));
	comboBoxLanguage.setEnabled(false);
		
	comboBoxEyeType = new JComboBox();
	comboBoxEyeType.setFont(new Font("SansSerif", Font.PLAIN, 11));
	comboBoxEyeType.setEnabled(false);

	btnWakeup = new JButton("Wake-Up");
	btnWakeup.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnWakeup.setVisible(false);
	btnWakeup.setEnabled(false);
	btnWakeup.setActionCommand("Wakeup");
	btnWakeup.addActionListener(this);

		btnSleep = new JButton("Sleep");
		btnSleep.setFont(new Font("SansSerif", Font.PLAIN, 11));
		btnSleep.setEnabled(false);
		btnSleep.addActionListener(this);
		btnSleep.setActionCommand("Sleep");

	GroupLayout gl_panelCameraControl = new GroupLayout(panelCameraControl);
	gl_panelCameraControl.setHorizontalGroup(gl_panelCameraControl.createParallelGroup(Alignment.LEADING).addGroup(
		gl_panelCameraControl.createSequentialGroup().addGroup(
			gl_panelCameraControl.createParallelGroup(Alignment.TRAILING).addGroup(
				gl_panelCameraControl.createSequentialGroup().addGroup(
					gl_panelCameraControl.createParallelGroup(Alignment.LEADING).addComponent(lblLanguage).addComponent(lblSound).addComponent(lblLED)).addGap(18).addGroup(
					gl_panelCameraControl.createParallelGroup(Alignment.LEADING).addComponent(comboBoxLED, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBoxSound, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE).addComponent(comboBoxLanguage, GroupLayout.PREFERRED_SIZE,
							158, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED, 29, Short.MAX_VALUE).addGroup(
					gl_panelCameraControl.createParallelGroup(Alignment.LEADING).addComponent(btnSetLanguage, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE).addComponent(
						btnPlaySound, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE).addComponent(btnSetLED, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))).addGroup(
				gl_panelCameraControl.createSequentialGroup().addGap(24).addComponent(btnWakeup).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnSleep,
					GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(lblVolume).addPreferredGap(ComponentPlacement.RELATED).addComponent(sliderVolume, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)))
			.addGap(12)));
	gl_panelCameraControl.setVerticalGroup(gl_panelCameraControl.createParallelGroup(Alignment.TRAILING).addGroup(
		gl_panelCameraControl.createSequentialGroup().addGroup(
			gl_panelCameraControl.createParallelGroup(Alignment.LEADING, false).addGroup(
				gl_panelCameraControl.createSequentialGroup().addGroup(
					gl_panelCameraControl.createParallelGroup(Alignment.BASELINE, false).addComponent(btnWakeup).addComponent(lblVolume).addComponent(btnSleep)).addGap(2))
				.addComponent(sliderVolume, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGroup(
			gl_panelCameraControl.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panelCameraControl.createSequentialGroup().addGap(6).addGroup(
					gl_panelCameraControl.createParallelGroup(Alignment.BASELINE).addComponent(lblLanguage).addComponent(comboBoxLanguage, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))).addGroup(
				gl_panelCameraControl.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED).addComponent(btnSetLanguage))).addGap(0).addGroup(
			gl_panelCameraControl.createParallelGroup(Alignment.TRAILING).addGroup(
				gl_panelCameraControl.createSequentialGroup().addGroup(
					gl_panelCameraControl.createParallelGroup(Alignment.BASELINE).addComponent(lblSound).addComponent(btnPlaySound, GroupLayout.DEFAULT_SIZE,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(comboBoxSound, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(
					ComponentPlacement.RELATED).addComponent(lblLED)).addGroup(
				gl_panelCameraControl.createSequentialGroup().addGap(21).addGroup(
					gl_panelCameraControl.createParallelGroup(Alignment.BASELINE).addComponent(btnSetLED).addComponent(comboBoxLED, GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED))).addGap(1)));
	gl_panelCameraControl.setAutoCreateGaps(false);
	panelCameraControl.setLayout(gl_panelCameraControl);

	chckbxShowLiveImages = new JCheckBox("Show Live Images");
	chckbxShowLiveImages.setFont(new Font("SansSerif", Font.PLAIN, 11));
	chckbxShowLiveImages.setEnabled(false);
	chckbxShowLiveImages.setSelected(true);
	chckbxShowLiveImages.addChangeListener(new ChangeListener()
	{
	    /**
	     * This Method will either enable or disable the Live Images
	     * depending upon the value of the chckbxShowLiveImages. the
	     * SetLive() method of the TD100.java class is called with input
	     * parameter.
	     */
	    public void stateChanged(ChangeEvent arg0)
	    {
			if (chckbxShowLiveImages.isSelected())
			{
			    jtd100.SetLive(TD100Constants.LIVE_ENABLED);
			} 
			else
			{
			    jtd100.SetLive(TD100Constants.LIVE_DISABLED);
			    lblLiveImage.setIcon(null);
			}
	    }
	});

	
	btnCancel = new JButton("Cancel");
	btnCancel.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnCancel.setActionCommand("Cancel");
	btnCancel.addActionListener(this);
	btnCancel.setEnabled(false);

	panelIris = new JPanel();
	panelIris.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Iris", TitledBorder.LEADING, TitledBorder.TOP, font, Color.BLUE));

	GroupLayout gl_panelImageCapture = new GroupLayout(panelImageCapture);
	gl_panelImageCapture.setHorizontalGroup(gl_panelImageCapture.createParallelGroup(Alignment.TRAILING).addGroup(
		gl_panelImageCapture.createSequentialGroup().addContainerGap().addGroup(
			gl_panelImageCapture.createParallelGroup(Alignment.LEADING).addComponent(
				chckbxShowLiveImages)).addGap(18).addGroup(
			gl_panelImageCapture.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panelImageCapture.createSequentialGroup().addGap(38).addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)).addGroup(
				gl_panelImageCapture.createSequentialGroup().addGap(18).addGroup(
					gl_panelImageCapture.createParallelGroup(Alignment.LEADING).addComponent(
						panelIris, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)))).addGap(29)));
	gl_panelImageCapture.setVerticalGroup(gl_panelImageCapture.createParallelGroup(Alignment.LEADING).addGroup(gl_panelImageCapture.createSequentialGroup().addComponent(btnCancel).addGap(164))
		.addGroup(
			gl_panelImageCapture.createSequentialGroup().addGap(2).addComponent(chckbxShowLiveImages, GroupLayout.PREFERRED_SIZE, 16, Short.MAX_VALUE).addPreferredGap(
				ComponentPlacement.UNRELATED).addGroup(
				gl_panelImageCapture.createParallelGroup(Alignment.LEADING).addGroup(
					gl_panelImageCapture.createSequentialGroup().addGap(6).addComponent(panelIris, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE).addGap(2)
						).addGroup(
					gl_panelImageCapture.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED))).addContainerGap()));
	gl_panelImageCapture.setHonorsVisibility(false);
	gl_panelImageCapture.setAutoCreateGaps(false);

	rdbtnEnrollment = new JRadioButton("Enrollment Image Mode");
	rdbtnEnrollment.setFont(new Font("SansSerif", Font.PLAIN, 11));
	rdbtnEnrollment.setEnabled(false);
	rdbtnEnrollment.setSelected(true);

	rdbtnRecognition = new JRadioButton("Recognition Image Mode");
	rdbtnRecognition.setFont(new Font("SansSerif", Font.PLAIN, 11));
	rdbtnRecognition.setEnabled(false);
	rdbtnRecognition.setSelected(false);
	
	bgroup = new ButtonGroup();
	bgroup.add(rdbtnEnrollment);
	bgroup.add(rdbtnRecognition);

	btnCaptureIris = new JButton("Capture Iris");
	btnCaptureIris.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnCaptureIris.setActionCommand("CaptureIris");
	btnCaptureIris.addActionListener(this);
	btnCaptureIris.setEnabled(false);

	GroupLayout gl_panelIris = new GroupLayout(panelIris);
	gl_panelIris.setHorizontalGroup(gl_panelIris.createParallelGroup(Alignment.LEADING).addGroup(
		gl_panelIris.createSequentialGroup().addGroup(
			gl_panelIris.createParallelGroup(Alignment.LEADING).addComponent(comboBoxEyeType).addComponent(rdbtnRecognition).addComponent(rdbtnEnrollment).addGroup(
				gl_panelIris.createSequentialGroup().addGap(21).addComponent(btnCaptureIris, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE))).addContainerGap(
			GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	gl_panelIris.setVerticalGroup(gl_panelIris.createParallelGroup(Alignment.TRAILING).addGroup(
		Alignment.LEADING,
		gl_panelIris.createSequentialGroup().addComponent(comboBoxEyeType).addComponent(rdbtnEnrollment).addPreferredGap(ComponentPlacement.RELATED).addComponent(rdbtnRecognition).addPreferredGap(
			ComponentPlacement.RELATED).addComponent(btnCaptureIris).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	gl_panelIris.setAutoCreateGaps(false);
	panelIris.setLayout(gl_panelIris);

	chckbxShowFaceBox = new JCheckBox("Show Face Box");
	chckbxShowFaceBox.setFont(new Font("SansSerif", Font.PLAIN, 11));
	chckbxShowFaceBox.setSelected(true);
	chckbxShowFaceBox.setEnabled(false);
	chckbxShowFaceBox.addItemListener(new ItemListener()
	{
	    public void itemStateChanged(ItemEvent e)
	    {
			toggle = (e.getStateChange() == ItemEvent.SELECTED);
			//if (toggle)
			{
			    chkFaceBox_OnClick();
			}
	    }

	});

	lblInnerWidth = new JLabel("Inner Width");
	lblInnerWidth.setFont(new Font("SansSerif", Font.PLAIN, 11));

	txtFieldInnerWidth = new JTextField();
	txtFieldInnerWidth.setFont(new Font("SansSerif", Font.PLAIN, 11));
	txtFieldInnerWidth.setDocument(new FixedSizeDocument(4));
	txtFieldInnerWidth.setText("360");
	txtFieldInnerWidth.setEnabled(false);
	txtFieldInnerWidth.setColumns(4);

	lblInnerHeight = new JLabel("Inner Height");
	lblInnerHeight.setFont(new Font("SansSerif", Font.PLAIN, 11));

	txtFieldInnerHeight = new JTextField(4);
	txtFieldInnerHeight.setFont(new Font("SansSerif", Font.PLAIN, 11));
	txtFieldInnerHeight.setDocument(new FixedSizeDocument(4));
	txtFieldInnerHeight.setText("520");
	txtFieldInnerHeight.setEnabled(false);
	txtFieldInnerHeight.setColumns(4);

	lbOuterWidth = new JLabel("Outer Width");
	lbOuterWidth.setFont(new Font("SansSerif", Font.PLAIN, 11));

	txtFieldOuterWidth = new JTextField(4);
	txtFieldOuterWidth.setFont(new Font("SansSerif", Font.PLAIN, 11));
	txtFieldOuterWidth.setDocument(new FixedSizeDocument(4));
	txtFieldOuterWidth.setText("480");
	txtFieldOuterWidth.setEnabled(false);
	txtFieldOuterWidth.setColumns(4);

	lblOuterHeight = new JLabel("Outer Height");
	lblOuterHeight.setFont(new Font("SansSerif", Font.PLAIN, 11));

	txtFieldOuterHeight = new JTextField(4);
	txtFieldOuterHeight.setFont(new Font("SansSerif", Font.PLAIN, 11));
	txtFieldOuterHeight.setDocument(new FixedSizeDocument(4));
	txtFieldOuterHeight.setText("640");
	txtFieldOuterHeight.setEnabled(false);
	txtFieldOuterHeight.setColumns(4);

	btnTakePicture = new JButton("Take Picture");
	btnTakePicture.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnTakePicture.setVisible(false);
	btnTakePicture.setActionCommand("TakePicture");
	btnTakePicture.addActionListener(this);
	btnTakePicture.setEnabled(false);

	btnCaptureFace = new JButton("Capture Face");
	btnCaptureFace.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnCaptureFace.setEnabled(false);
	btnCaptureFace.setActionCommand("CaptureFace");
	btnCaptureFace.addActionListener(this);

	btnTakeScene = new JButton("Take Picture");
	btnTakeScene.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnTakeScene.setActionCommand("TakeScene");
	btnTakeScene.addActionListener(this);
	btnTakeScene.setEnabled(false);
	btnTakeScene.setVisible(false);

	btnCaptureScene = new JButton("Capture Scene");
	btnCaptureScene.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnCaptureScene.setEnabled(false);
	btnCaptureScene.setActionCommand("CaptureScene");
	btnCaptureScene.addActionListener(this);
	panelImageCapture.setLayout(gl_panelImageCapture);

	panelLiveImages = new JPanel();
	panelLiveImages.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
	panelLiveImages.setPreferredSize(new Dimension(310, 225));
	panelLiveImages.setMaximumSize(new Dimension(310, 225));
	panelLiveImages.setMinimumSize(new Dimension(310, 225));
	panelLiveImages.repaint();

	lblRange = new JLabel("Range:");
	lblRange.setFont(new Font("SansSerif", Font.PLAIN, 11));

	lblVelocity = new JLabel("Velocity:");
	lblVelocity.setFont(new Font("SansSerif", Font.PLAIN, 11));

	lblDistance = new JLabel("Distance:");
	lblDistance.setFont(new Font("SansSerif", Font.PLAIN, 11));

	lblRangeValue = new JLabel();
	lblRangeValue.setFont(new Font("SansSerif", Font.PLAIN, 11));
	lblRangeValue.setEnabled(false);
	
	lblVelocityValue = new JLabel();
	lblVelocityValue.setFont(new Font("SansSerif", Font.PLAIN, 11));
	lblVelocityValue.setEnabled(false);
	
	lblDistanceValue = new JLabel();
	lblDistanceValue.setEnabled(false);
	lblDistanceValue.setFont(new Font("SansSerif", Font.PLAIN, 11));
	
	
	lblLiveImage = new JLabel("");
	lblLiveImage.setPreferredSize(new Dimension(310, 225));
	lblLiveImage.setMaximumSize(new Dimension(310, 225));
        lblLiveImage.setMinimumSize(new Dimension(310, 225));
	lblLiveImage.repaint();

	GroupLayout gl_panelLiveImages = new GroupLayout(panelLiveImages);
	gl_panelLiveImages.setHorizontalGroup(gl_panelLiveImages.createParallelGroup(Alignment.LEADING).addComponent(lblLiveImage, GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE));
	gl_panelLiveImages.setVerticalGroup(gl_panelLiveImages.createParallelGroup(Alignment.TRAILING).addComponent(lblLiveImage, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE));
	panelLiveImages.setLayout(gl_panelLiveImages);
	panelCamera.setLayout(null);

	lblSerialNumber = new JLabel("S/N:");
	lblSerialNumber.setFont(new Font("SansSerif", Font.PLAIN, 11));
	lblSerialNumber.setBounds(10, 19, 27, 14);
	panelCamera.add(lblSerialNumber);

	txtSerialNumber = new JTextField();
	txtSerialNumber.setFont(new Font("SansSerif", Font.PLAIN, 10));

	txtSerialNumber.setBounds(34, 17, 117, 20);
	txtSerialNumber.setHorizontalAlignment(SwingConstants.LEFT);
	txtSerialNumber.setEditable(false);
	panelCamera.add(txtSerialNumber);
	txtSerialNumber.setColumns(16);

	lblVersion = new JLabel("Ver:");
	lblVersion.setFont(new Font("SansSerif", Font.PLAIN, 11));
	lblVersion.setBounds(10, 42, 27, 20);
	panelCamera.add(lblVersion);

	btnConnect = new JButton("Connect");
	btnConnect.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnConnect.addActionListener(this);
	btnConnect.setActionCommand("Connect");
	btnConnect.setBounds(161, 15, 99, 23);
	panelCamera.add(btnConnect);

	btnDisconnect = new JButton("Disconnect");
	btnDisconnect.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnDisconnect.setEnabled(false);
	btnDisconnect.setActionCommand("Disconnect");
	btnDisconnect.addActionListener(this);

	btnDisconnect.setBounds(270, 15, 109, 23);
	panelCamera.add(btnDisconnect);

	txtVersion = new JTextField();
	txtVersion.setFont(new Font("SansSerif", Font.PLAIN, 11));
	txtVersion.setEditable(false);
	txtVersion.setBounds(34, 42, 117, 20);
	panelCamera.add(txtVersion);
	txtVersion.setColumns(10);

	cmbVersion = new JComboBox();
	cmbVersion.setFont(new Font("SansSerif", Font.PLAIN, 11));
	cmbVersion.setEnabled(false);
	cmbVersion.setBounds(161, 42, 99, 20);
	panelCamera.add(cmbVersion);

	btnGetVersion = new JButton("Get Version");
	btnGetVersion.setFont(new Font("SansSerif", Font.PLAIN, 11));
	btnGetVersion.setEnabled(false);
	btnGetVersion.addActionListener(this);
	btnGetVersion.setActionCommand("GetVersion");
	btnGetVersion.setBounds(270, 41, 109, 23);
	panelCamera.add(btnGetVersion);
	contentPaneIrisSampleApp.setLayout(gl_contentPaneIrisSampleApp);
    }
    
    /**
     * OnGetFaceImage() gets a face image as PressButton() is called after
     * StartCapture() with MODE_FACE. The Image is resized to display it
     * appropriately.
     */
    public void OnGetFaceImage(byte[] bmp)
    {
	// TODO Auto-generated method stub

	try
	{
	    faceBMP = bmp;
	    InputStream is = new ByteArrayInputStream(bmp);
	   
	    BufferedImage image = ImageIO.read(is);
	    BufferedImage img = new BufferedImage(1600, 1200, BufferedImage.TYPE_INT_RGB);
	    Graphics g = img.getGraphics();
	    g.drawImage(image, 0, 0, null);
	    int xCoordinateOuter = (int) (image.getWidth() / 2 - outputWidthValue / 2);
	    int yCoordinateOuter = (int) (image.getHeight() / 2 - outputHeightValue / 2);
	    Image resizedImage = img.getSubimage(xCoordinateOuter, yCoordinateOuter, (int) outputWidthValue, (int) outputHeightValue);
	    Image scaledImage = resizedImage.getScaledInstance(lblFaceCaptureImage.getWidth(), lblFaceCaptureImage.getHeight(), Image.SCALE_DEFAULT);
	    lblFaceCaptureImage.setIcon(new ImageIcon(scaledImage));
	    chckbxSaveFaceImage.setEnabled(true);
	    btnSaveSelectedImages.setEnabled(true);
	    btnCaptureFace.setVisible(true);
	    btnTakePicture.setEnabled(true);
	    btnTakePicture.setVisible(false);
	    components = panelIris.getComponents();
	    for (int comp = 0; comp < components.length; comp++)
	    {
		components[comp].setEnabled(true);
	    }
	    
	    chckbxShowFaceBox.setEnabled(false);
	    txtFieldInnerWidth.setEnabled(true);
	    txtFieldInnerHeight.setEnabled(true);
	    txtFieldOuterWidth.setEnabled(true);
	    txtFieldOuterHeight.setEnabled(true);
	    chckbxShowFaceBox.setSelected(true);
	    lblLiveImage.setIcon(null);

	    /* Fix for Elementool issue #629::Enable Sleep button *////
	    btnSleep.setEnabled(true);

	} catch (Exception e)
	{
	    System.out.println("MainFrame.java::Error in OnGetFaceImage");
	    e.printStackTrace();
	}
	
    }
    
    @Override
	public long SetCallback(TD100 jtd100) {
		// TODO Auto-generated method stub
		return (jtd100.SetCallback(this));
	}

    /**
     * After calling StartCapture() function with MODE_IRIS_ENROLL /
     * MODE_IRIS_RECOG, iris images will be automatically captured and returned
     * through this API function when a user is properly positioned within the
     * operating range of camera. Manual image capture is also possible by
     * calling PressButton() or pressing the shutter button. In this case if the
     * captured images do not include iris, PressButton() returns an error value
     * instead of giving iris images through OnGetIrisImage(). OnGetIrisImage()
     * method passes the raw data which we get from the SDK to the saveBitmap()
     * of BMPFile.java which converts it to a BMP image by adding BMPHeader and
     * BMPHeaderInfo.
     */
    
    public void OnGetIrisImage(byte[] rightRawImage, byte[] leftRawImage)
    {
	// TODO Auto-generated method stub

	if (irisResult == 0)
	{
	    lblRangeValue.setText("");
	    lblVelocityValue.setText("");
	    lblDistanceValue.setText("");
	    rdbtnEnrollment.setEnabled(true);
	    btnCaptureFace.setEnabled(true);
	    components = panelIris.getComponents();
	    for (int comp = 0; comp < components.length; comp++)
	    {
		components[comp].setEnabled(true);
	    }

	    btnCaptureIris.setText("Capture Iris");
	}

	try
	{
	    rightRawImageSave = rightRawImage;
	    leftRawImageSave = leftRawImage;
	    
	    String currentdir = System.getProperty("user.dir");

	    if( rightRawImageSave != null ) {
		    String rightEye = "RightEye.bmp";
		    bmpFile.saveBitmap(rightEye, rightRawImage, 640, 480);
		    File rightIrisFile = new File(currentdir, "RightEye.bmp");
		    BufferedImage rightIris = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
		    BufferedImage rightIrisImage = ImageIO.read(rightIrisFile);
		    Graphics g1 = rightIris.getGraphics();
		    g1.drawImage(rightIrisImage, 0, 0, null);
		    Image resizedRight = rightIris.getScaledInstance(lblRightIrisCapture.getWidth(), lblRightIrisCapture.getHeight(), Image.SCALE_DEFAULT);
		    lblRightIrisCapture.setIcon(new ImageIcon(resizedRight));
		    rightIrisFile.delete();
	    }
	    
	    if( leftRawImageSave != null ) {
		    String leftEye = "LeftEye.bmp";
		    bmpFile.saveBitmap(leftEye, leftRawImage, 640, 480);
		    File leftIrisFile = new File(currentdir, "LeftEye.bmp");
		    BufferedImage leftIris = new BufferedImage(640, 480, BufferedImage.TYPE_3BYTE_BGR);
		    BufferedImage leftIrisImage = ImageIO.read(leftIrisFile);
		    Graphics g2 = leftIris.getGraphics();
		    g2.drawImage(leftIrisImage, 0, 0, null);
		    Image resizedLeft = leftIris.getScaledInstance(lblRightIrisCapture.getWidth(), lblRightIrisCapture.getHeight(), Image.SCALE_DEFAULT);
		    lblLeftIrisCapture.setIcon(new ImageIcon(resizedLeft));
		    leftIrisFile.delete();
	    }
	    
	    chckbxSaveIrisImages.setEnabled(true);
	    btnSaveSelectedImages.setEnabled(true);
	    lblLiveImage.setIcon(null);

	    /* Fix for Elementool issue #629::Enable Sleep button */
	    btnSleep.setEnabled(true);
	} catch (Exception e)
	{
	    System.out.println("MainFrame.java::Error in OnGetIrisImage");
	    e.printStackTrace();
	}

    }

    /**
     *OnGetLiveImage() gets live images. It includes distance information only
     * in MODE_IRIS_ENROLL / MODE_IRIS_RECOG of StartCapture(). This method is
     * called for MODE_IRIS_RECOG/MODE_IRIS_ENROLL,MODE_FACE and MODE_SCENE .
     * Guide boxes are imprinted in MODE_FACE and MODE_IRIS_ENROLL /
     * MODE_IRIS_RECOG
     */
    public void OnGetLiveImage(byte[] liveImage, long range, long distance)
    {
	// TODO Auto-generated method stub
	try
	{
		if( chckbxShowLiveImages.isSelected() == false )
			return;
		
		InputStream is = new ByteArrayInputStream(liveImage);
	    BufferedImage image = ImageIO.read(is);
	    /*
	     * Image is scaled as per the lblLiveImage width and height
	     */
	    Image resizedImage = image.getScaledInstance(lblLiveImage.getWidth(), lblLiveImage.getHeight(), Image.SCALE_SMOOTH);
	    Graphics g = image.getGraphics();
	    g.drawImage(image, 0, 0, null);
	    /*
	     * On checking the ShowFaceBox and when the mode if Face the Draw
	     * Rectangle function is called to show the face box on the Image
	     */
	    //if ((chckbxShowFaceBox.isSelected()) && (actionCommand.equalsIgnoreCase("CaptureFace")) && toggle_face)
	    if ((chckbxShowFaceBox.isSelected()) && faceCaptureInProgress && toggle_face)
	    {
		DrawRectangle(g, resizedImage, image.getWidth(), image.getHeight());
		lblLiveImage.setIcon(new ImageIcon(resizedImage));

	    } else
	    {

	    lblLiveImage.setIcon(new ImageIcon(resizedImage));
	    }
	  g.dispose();

	} catch (Exception ex)
	{
	    System.out.println("MainFrame.java::Error in OnGetLiveImage");
	    ex.printStackTrace();
	}

    }

    /**
     * OnGetLiveIrisInfo() gets the current information of distance, range, and
     * velocity of detected object in MODE_IRIS_ENROLL / MODE_IRIS_RECOG
     */
    public void OnGetLiveIrisInfo(long distance, long range, long velocity, long lNone)
    {
	// TODO Auto-generated method stub
	try
	{
 	    
	    String range1 = Long.toString(range);
	   
	    if (Integer.parseInt(range1) == 1)
	    {
		 lblRangeValue.setText("OUT");
	    } else if (Integer.parseInt(range1) == 2)
	    {
		lblRangeValue.setText("NEAR");
	    } else if (Integer.parseInt(range1) == 3)
	    {
		lblRangeValue.setText("FAR");
	    } else
	    {
		lblRangeValue.setText("OPERATING");
	    }
	    
	    String velocity1 = "" + velocity;
	    lblVelocityValue.setText(velocity1);
	    String distance1 = "" + distance;
	    lblDistanceValue.setText(distance1);
	} catch (Exception e)
	{
	    System.out.print("MainFrame.java::Error in OnGetLiveIrisInfo");
	    e.printStackTrace();
	}
    }

    /**
     * OnGetSceneImage() gets a scene image as PressButton() is called after
     * StartCapture() with MODE_SCENE.
     */

    public void OnGetSceneImage(byte[] bmp)
    {
	// TODO Auto-generated method stub

	try
	{
	    sceneBMP = bmp;
	    InputStream is = new ByteArrayInputStream(bmp);
	    BufferedImage image = ImageIO.read(is);
	    BufferedImage img = new BufferedImage(1600, 1200, BufferedImage.TYPE_INT_RGB);
	    Graphics g = img.getGraphics();
	    g.drawImage(image, 0, 0, null);
	    Image scaledImage = img.getScaledInstance(lblSceneCapture.getWidth(), lblSceneCapture.getHeight(), Image.SCALE_DEFAULT);
	    lblSceneCapture.setIcon(new ImageIcon(scaledImage));
	    chckbxSaveSceneImage.setEnabled(true);
	    btnSaveSelectedImages.setEnabled(true);
	    btnCaptureScene.setVisible(true);
	    btnTakeScene.setEnabled(true);
	    btnTakeScene.setVisible(false);
	    components = panelIris.getComponents();
	    for (int comp = 0; comp < components.length; comp++)
	    {
		components[comp].setEnabled(true);
	    }
	    chckbxShowFaceBox.setEnabled(false);
	    lblLiveImage.setIcon(null);
	    
	    /* Fix for Elementool issue #629::Enable Sleep button */
	    btnSleep.setEnabled(true);
	} catch (Exception e)
	{
	    System.out.print("MainFrame.java::Error in OnGetSceneImage");
	    e.printStackTrace();

	}
    }

    /**
     * OnGetStatus() gets the status of the camera when the status has changed.
     */
    public void OnGetStatus(long statusType, long statusCode)
    {
	// TODO Auto-generated method stub

	if (statusType == 1)
	{
	    String message = "Camera was disconnected";
	    Disconnect(true);
	    ShowDialogBox(message);

	}
	if (statusType == 2)
	{
		    /*  
	    jtd100.StartCapture(TD100Constants.IMG_MODE_SCENE);
	    lblRangeValue.setText("");
	    lblVelocityValue.setText("");
	    lblDistanceValue.setText("");
	    rdbtnEnrollment.setEnabled(true);
	    btnCaptureFace.setEnabled(true);
	    components = panelScene.getComponents();
	    for (int comp = 0; comp < components.length; comp++)
	    {
		components[comp].setEnabled(true);
	    }
	    components = panelIris.getComponents();
	    for (int comp = 0; comp < components.length; comp++)
	    {
		components[comp].setEnabled(true);
	    }
	    */

	    //String message = "Cannot take iris images.Check if it was in operating range";
	    //ShowDialogBox(message);
	    SwingUtilities.invokeLater(new Runnable() {
	           public void run() {
	               JOptionPane.showMessageDialog(null, "Cannot take iris images.Check if it was in operating range");
		       CaptureIris();
	           }
	        });


	}

	if (statusType == 3)
	{
	    Wakeup();
	}

	if (statusType == 4)
	{
	    String message = "Camera has an error";
	    ShowDialogBox(message);
	    Disconnect(false);
	}

	if (statusType == 5)
	{
	    String message = "The camera is unable to capture image(s) or unable to process internally captured image(s)";
	    ShowDialogBox(message);
	    jtd100.StartCapture(TD100Constants.IMG_MODE_SCENE);
	}
    }

    private void TakePicture() {
    	irisResult = jtd100.PressButton();
		
		if( irisResult != 0 ) {
			//Enable all the controls in panelIris
		    components = panelIris.getComponents();
		    for (int comp = 0; comp < components.length; comp++)
		    {
		    	components[comp].setEnabled(true);
		    }
		    		    
		    //Enable all the controls in panelScene
		    
		    btnCaptureFace.setEnabled(true);
		    btnCaptureIris.setText("Capture Iris");
		    ShowDialogBox("MainFrame::Error while taking picture. Error: " + irisResult);
	    	return;
	    }
    }
    
    private void CaptureIris() {
	
		faceCaptureInProgress = false; /* #650 issue fix facapture box not to display*/
    	lblRightIrisCapture.setIcon(null);
	    lblLeftIrisCapture.setIcon(null);
	    btnCaptureIris.setText("Take Picture");

	    if (rdbtnEnrollment.isSelected() == true)
	    {
	    	enrollmentMode = true;
	    	switch(comboBoxEyeType.getSelectedIndex()) {
	    	case 0:
	    		irisResult = jtd100.StartCapture(TD100Constants.IMG_MODE_IRIS_ENROLL);
	    		break;
	    	case 1:
	    		irisResult = jtd100.StartCapture(TD100Constants.IMG_MODE_RIGHT_IRIS_ENROLL);
	    		break;
	    	case 2:
	    		irisResult = jtd100.StartCapture(TD100Constants.IMG_MODE_LEFT_IRIS_ENROLL);
	    		break;
	    	}
	    	
	    }

	    if (rdbtnRecognition.isSelected() == true)
	    {
	    	recognitionMode = true;
	    	switch(comboBoxEyeType.getSelectedIndex()) {
	    	case 0:
	    		irisResult = jtd100.StartCapture(TD100Constants.IMG_MODE_IRIS_RECOG);
	    		break;
	    	case 1:
	    		irisResult = jtd100.StartCapture(TD100Constants.IMG_MODE_RIGHT_IRIS_RECOG);
	    		break;
	    	case 2:
	    		irisResult = jtd100.StartCapture(TD100Constants.IMG_MODE_LEFT_IRIS_RECOG);
	    		break;
	    	case 3:
	    		irisResult = jtd100.StartCapture(TD100Constants.IMG_MODE_IRIS_RECOG_EITHER);
	    		break;
	    	}
	    }
	    
	    if( irisResult != 0 ) {
	    	ShowDialogBox("MainFrame::Error in starting iris capture. Error: " + irisResult);
	    	return;
	    }
	    
	    //Disable all the controls in panelIris
	    components = panelIris.getComponents();
	    for (int comp = 0; comp < components.length; comp++)
	    {
	    	components[comp].setEnabled(false);
	    }
	    		    
	    //Disable all the controls in panelScene
	    
	    btnCaptureFace.setEnabled(false);
	    btnCaptureIris.setEnabled(true);
    }

    /**
     * All the actions performed in the UI are implemented in the
     * actionPerformed() method.
     */

    public void actionPerformed(ActionEvent evt)
    {
	actionCommand = evt.getActionCommand();

	// Action performed to call GetVersion()
	if (actionCommand.equalsIgnoreCase("GetVersion"))
	{
	    GetVersion();
	}

	// Action to call Disconnect()
	if (actionCommand.equalsIgnoreCase("Disconnect"))
	{
	    Disconnect(false);
	    System.out.println("Connection Closed Successfully");
	}

	// Action to call Connect()
	if (actionCommand.equalsIgnoreCase("Connect"))
	{
	    Connect();
	}

	// Action to call CaptureFace

	if (actionCommand.equalsIgnoreCase("CaptureFace"))
	{
	    try {
			tempinputWidthValue = Long.parseLong(txtFieldInnerWidth.getText());
			tempinputHeightValue = Long.parseLong(txtFieldInnerHeight.getText());
			tempoutputWidthvalue = Long.parseLong(txtFieldOuterWidth.getText());
			tempoutputHeightValue = Long.parseLong(txtFieldOuterHeight.getText());
	    } 
	    catch (Exception ex) {
			ShowDialogBox("Input string was not in a correct format");
			return;
	    }
	    
	    if ((tempinputWidthValue < MIN_FACE_BOX_IN_WIDTH) || (tempinputWidthValue > MAX_FACE_BOX_IN_WIDTH)
		    || (tempinputHeightValue < MIN_FACE_BOX_IN_HEIGHT) || (tempinputHeightValue > MAX_FACE_BOX_IN_HEIGHT)
		    || (tempoutputWidthvalue < MIN_FACE_BOX_OUT_X) || (tempoutputWidthvalue > MAX_FACE_BOX_OUT_X)
		    || (tempoutputHeightValue < MIN_FACE_BOX_OUT_Y) || (tempoutputHeightValue > MAX_FACE_BOX_OUT_Y)) {
	    	ShowDialogBox("Please enter the values in between the range specified" + "\n Inner Width: (48 ~ 1232)" + "\nInner Height: (48 ~ 912)" + "\nOuter Width: (96 ~ 1280)"
	    			+ "\nOuter Height: (96 ~ 960).");
	    	return;

	    } 
	    else {
		inputWidthValue = tempinputWidthValue;
		inputHeightValue = tempinputHeightValue;
		outputWidthValue = tempoutputWidthvalue;
		outputHeightValue = tempoutputHeightValue;
	   
	    btnCaptureFace.setVisible(false);
	    btnTakePicture.setVisible(true);
	    
	    components = panelIris.getComponents();
	    for (int comp = 0; comp < components.length; comp++)
	    {
		components[comp].setEnabled(false);
	    }
	    chckbxShowFaceBox.setEnabled(true);
		txtFieldInnerWidth.setEnabled(false);
		txtFieldInnerHeight.setEnabled(false);
		txtFieldOuterWidth.setEnabled(false);
		txtFieldOuterHeight.setEnabled(false);
	    
		/* Fix for Elementool issue #629::Disable Sleep button */
		btnSleep.setEnabled(false);
	    
	    jtd100.StartCapture(TD100Constants.IMG_MODE_FACE);
		jtd100.SetLCDFaceGuideBounds(inputWidthValue, inputHeightValue, outputWidthValue, outputHeightValue);
	    toggle_face = true;
	    faceCaptureInProgress = true;
	    }
	}

	// Action to perform TakePicture in Capture Face mode
	if (actionCommand.equalsIgnoreCase("TakePicture"))
	{
	    btnTakePicture.setEnabled(false);
	    jtd100.PressButton();
	    toggle_face = false;
	    faceCaptureInProgress = false;
	}

	// Action to call CaptureScene

	if (actionCommand.equalsIgnoreCase("CaptureScene"))
	{
	    btnCaptureScene.setVisible(false);
	    btnTakeScene.setVisible(true);
	    btnCaptureFace.setEnabled(false);
	    components = panelIris.getComponents();
	    for (int comp = 0; comp < components.length; comp++)
	    {
		components[comp].setEnabled(false);
	    }

	    /* Fix for Elementool issue #629::Disable Sleep button */
	    btnSleep.setEnabled(false);
	    
	    jtd100.StartCapture(TD100Constants.IMG_MODE_SCENE);

	}

	// Action to call Take Scene in Scene Mode
	if (actionCommand.equalsIgnoreCase("TakeScene"))
	{
	    btnTakeScene.setEnabled(false);
	    jtd100.PressButton();

	}

	// Action to call Capture Iris
	if (actionCommand.equalsIgnoreCase("CaptureIris"))
	{
		/* Fix for Elementool issue #629::Disable Sleep button */
		btnSleep.setEnabled(false);
	    
		if( btnCaptureIris.getText().compareTo("Capture Iris") == 0 ) {
			CaptureIris();
		}
		else {
			TakePicture();
		}
	}

	// Action to call Cancel
	if (actionCommand.equalsIgnoreCase("Cancel"))
	{
		toggle_face = false;
		/* Fix for Elementool issue #648:: set the capture mode to scene so that Irisliveinfo events will not be received*/
		jtd100.StartCapture(TD100Constants.IMG_MODE_SCENE);
   	    btnTakeScene.setVisible(false);
	    btnTakePicture.setVisible(false);
	    btnCaptureFace.setVisible(true);
	    btnCaptureScene.setVisible(true);
	    lblRightIrisCapture.setIcon(null);
	    lblLeftIrisCapture.setIcon(null);
	    lblFaceCaptureImage.setIcon(null);
	    lblSceneCapture.setIcon(null);
	    
	    chckbxSaveFaceImage.setEnabled(false);
	    btnSaveSelectedImages.setEnabled(false);
	    chckbxSaveSceneImage.setEnabled(false);
	    chckbxSaveIrisImages.setEnabled(false);
	    chckbxSaveFaceImage.setSelected(false);
	    chckbxSaveSceneImage.setSelected(false);
	    chckbxSaveIrisImages.setSelected(false);
	    btnCaptureIris.setText("Capture Iris");
	    	    
	    components = panelIris.getComponents();
	    for (int comp = 0; comp < components.length; comp++)
	    {
		components[comp].setEnabled(true);
	    }
	   
	    chckbxShowFaceBox.setEnabled(false);
	    txtFieldInnerWidth.setEnabled(true);
	    txtFieldInnerHeight.setEnabled(true);
	    txtFieldOuterWidth.setEnabled(true);
	    txtFieldOuterHeight.setEnabled(true);
	    chckbxShowFaceBox.setSelected(true);
	    chckbxSaveIrisImages.setSelected(false);
	    chckbxSaveFaceImage.setSelected(false);
	    chckbxSaveSceneImage.setSelected(false);
	    faceCaptureInProgress = false;
	    
	    /* Fix for Elementool issue #629::Enable Sleep button */
	    btnSleep.setEnabled(true);	   
	    /* Fix for Elementool issue #648:: set the parameter values to emplty when user clicks on cancel and removed from the Capture Iris click event*/
		lblRangeValue.setText(" ");
	    lblVelocityValue.setText(" ");
	    lblDistanceValue.setText(" ");
	}

	// Action to call PlaySound

	if (actionCommand.equalsIgnoreCase("PlaySound"))
	{

	    int playMessageIndex = comboBoxSound.getSelectedIndex();
	    long playMessageResult = jtd100.PlayMessage(playMessageIndex);
	    if (0 != playMessageResult)
	    {
		ShowDialogBox("MainFrame::Error in playing Sound" + playMessageResult);
	    }
	}

	// Action to Set LED
	if (actionCommand.equalsIgnoreCase("SetLED"))
	{
	    int setLED = comboBoxLED.getSelectedIndex();
	    long setLEDResult = jtd100.SetLED(setLED + 1);
	    if (0 != setLEDResult)
	    {
		ShowDialogBox("MainFrame::Error in Setting LED" + setLEDResult);
	    }

	}

	// Action to Set Language
	if (actionCommand.equalsIgnoreCase("SetLanguage"))
	{
	    int setLanguageID = comboBoxLanguage.getSelectedIndex();
	    //for (languageIndex = 0; languageIndex < listOfLanguages.length; languageIndex++)
	    {
		    long setLangResult = jtd100.SetLanguage(setLanguageID + 1);
		    comboBoxLanguage.setSelectedIndex(setLanguageID);
		    if (0 != setLangResult)
		    {
		    	ShowDialogBox("MainFrame::Error in Setting Language" + setLangResult);
		    }
	    }

	}

	// Action to call Sleep method
	if (actionCommand.equalsIgnoreCase("Sleep"))
	{
	    Sleep();

	}

	// Action to call wakeup
	if (actionCommand.equalsIgnoreCase("Wakeup"))
	{
	    Wakeup();
	}

	// Action to saveImages
	if (actionCommand.equalsIgnoreCase("SaveImages"))
	{
	    
	    SaveImage();
	    
	}

    }

    /**
     * Method to call close() method of TD100.java. The proper implementation of
     * enabling and disabling the functionality is done here.
     * 
     */

    public void Disconnect(boolean bClosing)
    {
		long closeResult = jtd100.Close();
		
		faceCaptureInProgress = false;
		
		if (0 != closeResult && bClosing == false)
		{
		    ShowDialogBox("MainFrame::Error in Closing the iCAM Connection" + closeResult);
		    return;
		}
		
		components = panelCamera.getComponents();
		for (int comp = 0; comp < components.length; comp++)
		{
		    components[comp].setEnabled(false);
		    btnConnect.setEnabled(true);
		}
		components = panelImageCapture.getComponents();
		for (int comp = 0; comp < components.length; comp++)
		{
		    components[comp].setEnabled(false);
		}
		components = panelIris.getComponents();
		for (int comp = 0; comp < components.length; comp++)
		{
		    components[comp].setEnabled(false);
		}
		components = panelCameraControl.getComponents();
		for (int comp = 0; comp < components.length; comp++)
		{
		    components[comp].setEnabled(false);
		}
	
		components = panelLiveImages.getComponents();
		for (int comp = 0; comp < components.length; comp++)
		{
		  
		    components[comp].setEnabled(false);
		    
		}
		lblRangeValue.setText("");
		lblVelocityValue.setText("");
		lblDistanceValue.setText("");
 
		lblLiveImage.setIcon(null);
		lblRightIrisCapture.setIcon(null);
		lblLeftIrisCapture.setIcon(null);
		lblFaceCaptureImage.setIcon(null);
		btnSaveSelectedImages.setEnabled(false);


    }

    /**
     * Method to call Wakeup() method of TD100.java. All the different functions
     * which needs to be enabled when Wakeup() is called is handled here.
     * 
     * 
     */

    public void Wakeup()
    {
	toggle_face = false;
	components = panelCamera.getComponents();
	for (int comp = 0; comp < components.length; comp++)
	{
	    components[comp].setEnabled(true);
	    btnConnect.setEnabled(false);
	}

	components = panelImageCapture.getComponents();
	for (int comp = 0; comp < components.length; comp++)
	{
	    components[comp].setEnabled(true);
	}
	components = panelIris.getComponents();
	for (int comp = 0; comp < components.length; comp++)
	{
	    components[comp].setEnabled(true);
	}
	components = panelCameraControl.getComponents();
	for (int comp = 0; comp < components.length; comp++)
	{
	    components[comp].setEnabled(true);
	}

	components = panelLiveImages.getComponents();
	for (int comp = 0; comp < components.length; comp++)
	{
	    components[comp].setEnabled(true);
	}

	

	
	components = panelRightIris.getComponents();
	for (int comp = 0; comp < components.length; comp++)
	{
	    components[comp].setEnabled(true);
	}
	components = panelLeftIris.getComponents();
	for (int comp = 0; comp < components.length; comp++)
	{
	    components[comp].setEnabled(true);
	}
	
	btnWakeup.setVisible(false);
	btnSleep.setVisible(true);
	btnSleep.setEnabled(true);
	 
	chckbxShowFaceBox.setEnabled(false);
	txtFieldInnerWidth.setEnabled(true);
	txtFieldInnerHeight.setEnabled(true);
	txtFieldOuterWidth.setEnabled(true);
	txtFieldOuterHeight.setEnabled(true);
	
	btnCaptureFace.setVisible(true);
	btnTakePicture.setVisible(false);
	btnTakeScene.setVisible(false);
	btnCaptureScene.setVisible(true);
	chckbxShowFaceBox.setSelected(true);
	
	lblRangeValue.setText("");
	lblVelocityValue.setText("");
	lblDistanceValue.setText("");
	
	jtd100.Wakeup();

    }

    /**
     * Method to call Sleep() method of TD100.java. Except Wakeup(),all other
     * functionality are disable on click of Sleep Button
     * 
     */

    public void Sleep()
    {
	components = panelCamera.getComponents();
	for (int comp = 0; comp < components.length; comp++)
	{
	    components[comp].setEnabled(true);
	    btnConnect.setEnabled(false);
	    btnDisconnect.setEnabled(true);

	}
	components = panelImageCapture.getComponents();
	for (int comp = 0; comp < components.length; comp++)
	{
	    components[comp].setEnabled(false);

	}
	components = panelIris.getComponents();
	for (int comp = 0; comp < components.length; comp++)
	{
	    components[comp].setEnabled(false);
	}
	components = panelCameraControl.getComponents();
	for (int comp = 0; comp < components.length; comp++)
	{
	    components[comp].setEnabled(false);

	}

	components = panelLiveImages.getComponents();
	for (int comp = 0; comp < components.length; comp++)
	{
	    components[comp].setEnabled(false);
	}
	jtd100.StartCapture(TD100Constants.IMG_MODE_SCENE);
	
	lblLiveImage.setIcon(null);
	lblRightIrisCapture.setIcon(null);
	lblLeftIrisCapture.setIcon(null);
	lblFaceCaptureImage.setIcon(null);
	lblSceneCapture.setIcon(null);
	
	btnSleep.setVisible(false);
	btnWakeup.setVisible(true);
	btnWakeup.setEnabled(true);
	chckbxSaveFaceImage.setSelected(false);
	chckbxSaveSceneImage.setSelected(false);
	chckbxSaveIrisImages.setSelected(false);
	chckbxSaveFaceImage.setEnabled(false);
	btnSaveSelectedImages.setEnabled(false);
	chckbxSaveSceneImage.setEnabled(false);
	chckbxSaveIrisImages.setEnabled(false);
	
	faceCaptureInProgress = false;
	
	long sleepResult = jtd100.Sleep();
	if (0 != sleepResult)
	{

	    ShowDialogBox("MainFrame::Error in Wakeup" + sleepResult);
	}

    }

    /**
     * Method to call Open() method of TD100.java. Except Connect Button,all
     * other functionality are enabled on click of Connect Button
     * 
     */
    public void Connect()
    {
	sbSerialNumber = new StringBuffer();
	openResult = -1;
	openResult = jtd100.Open(sbSerialNumber);
	if (openResult == 0)
	{
		SetCallback(jtd100);
		
	    toggle_face = false;
	    if (0 != jtd100.GetSavedLanguages(numberOfLanguages, arrayOfLanguageIDs))
	    {
	    	ShowDialogBox("Error in getting languages");
	    }
	    
	    //Reomove all languages before populating again
	    int itemCount = comboBoxLanguage.getItemCount();
	    for(int i=0;i<itemCount;i++){
	    	comboBoxLanguage.removeItemAt(0);
	    }
	    
	    for (int i = 0; i < numberOfLanguages[0]; i++)
	    {
			// User-defined language Ids are between 200 (User01) and
			// 255 (User56)
			if (arrayOfLanguageIDs[i] > 26 || arrayOfLanguageIDs[i] < 0)
	        {
			    String str = String.format("User0" + (arrayOfLanguageIDs[i] - 199));
	            comboBoxLanguage.addItem(str);
			} 
			else {
				comboBoxLanguage.addItem(listOfLanguages[arrayOfLanguageIDs[i]]);
		    }
	    }
	    
	    long[] currentLangID = new long[1];
	    if (0 != jtd100.GetLanguage(currentLangID))
		{
			ShowDialogBox("Error in getting current language");
		}
		if(currentLangID[0] > 255) currentLangID[0] = 0;
	    comboBoxLanguage.setSelectedIndex((int)currentLangID[0]);
	    
	    txtSerialNumber.setText(sbSerialNumber.toString());
	    jtd100.StartCapture(TD100Constants.IMG_MODE_SCENE);
	    jtd100.SetFocusPosition(1, 7);
	    chckbxSaveFaceImage.setSelected(false);
	    btnSaveSelectedImages.setEnabled(false);
	    chckbxSaveSceneImage.setSelected(false);
	    chckbxSaveIrisImages.setSelected(false);
	    components = panelCamera.getComponents();
	    
	    for (int comp = 0; comp < components.length; comp++) {
			components[comp].setEnabled(true);
			btnConnect.setEnabled(false);
	    }
	    
	    cmbVersion.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "FPGA", "FIRMWARE", "LIBRARY", "DRIVER", "SDK" }));
	    comboBoxEyeType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BOTH", "RIGHT", "LEFT"}));
	   
		    
	    components = panelImageCapture.getComponents();
	    for (int comp = 0; comp < components.length; comp++) {
	    	components[comp].setEnabled(true);
	    }
	    
	
	    
	    components = panelIris.getComponents();
	    for (int comp = 0; comp < components.length; comp++) {
	    	components[comp].setEnabled(true);
	    }
	    
	
	    components = panelCameraControl.getComponents();
	    for (int comp = 0; comp < components.length; comp++) {
	    	components[comp].setEnabled(true);
	    }

	    components = panelLiveImages.getComponents();
	    for (int comp = 0; comp < components.length; comp++){
	    	components[comp].setEnabled(true);
	    }

	  
	    components = panelRightIris.getComponents();
	    for (int comp = 0; comp < components.length; comp++) {
	    	components[comp].setEnabled(true);
	    }
	    
	    components = panelLeftIris.getComponents();
	    for (int comp = 0; comp < components.length; comp++) {
	    	components[comp].setEnabled(true);
	    }
	    
		btnSleep.setVisible(true);
	    btnWakeup.setVisible(false);
	    chckbxShowFaceBox.setEnabled(false);
	    txtFieldInnerWidth.setEnabled(true);
	    txtFieldInnerHeight.setEnabled(true);
	    txtFieldOuterWidth.setEnabled(true);
	    txtFieldOuterHeight.setEnabled(true);
	    /* #651 issue fix to display the capture iris button on connect success*/ 
	    btnCaptureIris.setVisible(true);
	    btnCaptureIris.setText("Capture Iris");
	    btnTakePicture.setVisible(false);
	    btnCaptureFace.setVisible(true);
	    btnTakeScene.setVisible(false);
	    btnCaptureScene.setVisible(true);
	    
	    chckbxShowFaceBox.setSelected(true);
	    chckbxShowLiveImages.setSelected(true);
	    rdbtnRecognition.setSelected(false);
	    rdbtnEnrollment.setSelected(true);
	    rdbtnEnrollment.addItemListener(new ItemListener()
		{
		    public void itemStateChanged(ItemEvent e)
		    {
		    	if( rdbtnEnrollment.isSelected() == true ) {
		    		comboBoxEyeType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BOTH", "RIGHT", "LEFT"}));
		    		comboBoxEyeType.setSelectedIndex(0);
		    	}	
		    	else {
		    		comboBoxEyeType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BOTH", "RIGHT", "LEFT", "EITHER"}));
		    		comboBoxEyeType.setSelectedIndex(3);
		    	}
		    }
		});

	    
	    comboBoxLED.setSelectedIndex(0);
	    txtFieldInnerWidth.setText("360");
	    txtFieldInnerHeight.setText("520");
	    txtFieldOuterWidth.setText("480");
	    txtFieldOuterHeight.setText("640");
	    
	    txtVersion.setText("");
	    cmbVersion.setSelectedIndex(4);
	    GetVersion();
	    
	    lblRangeValue.setText("");
	    lblVelocityValue.setText("");
	    lblDistanceValue.setText("");
	    
	} else
	{
	    String messsage = "Failed to open iCAM TD100 camera" + "\n[ERROR CODE:" + openResult + "].\n  Please try again.";
	    ShowDialogBox(messsage);
	}

    }

    /**
     * Method to call GetVersion() method of TD100.java. This method gets all
     * the version details available for the specific type in the SDK.
     * 
     */
    public void GetVersion()
    {
	String string = cmbVersion.getSelectedItem().toString();
	if (string.equalsIgnoreCase("FPGA"))
	{
	    versionNumber = new StringBuffer(); 
		if (0 == jtd100.GetVersion(TD100Constants.VERSION_FPGA, versionNumber))
	    {
	    StringBuffer stb = new StringBuffer();
	    stb.append(versionNumber.toString());
	    txtVersion.setText(stb.toString());
		} else
		{
		    txtVersion.setText("");
		    ShowDialogBox("Exception occured while getting version information.\n Cannot get versions.");
	    }
	    
	} else if (string.equalsIgnoreCase("FIRMWARE"))
	{
	    versionNumber = new StringBuffer();
		if (0 == jtd100.GetVersion(TD100Constants.VERSION_FIRMWARE, versionNumber))
	    {
	    StringBuffer stb = new StringBuffer();
	    stb.append(versionNumber.toString());
	    txtVersion.setText(stb.toString());
		} else
	    {
		    txtVersion.setText("");
		    ShowDialogBox("Exception occured while getting version information.\n Cannot get versions.");
	    }
	} else if (string.equalsIgnoreCase("LIBRARY"))
	{
		versionNumber = new StringBuffer();
		if (0 == jtd100.GetVersion(TD100Constants.VERSION_LIB, versionNumber))
	    {
	    StringBuffer stb = new StringBuffer();
	    stb.append(versionNumber.toString());
	    txtVersion.setText(stb.toString());
		} else
	    {
		    txtVersion.setText("");
		    ShowDialogBox("Exception occured while getting version information.\n Cannot get versions.");
	    }
	} else if (string.equalsIgnoreCase("DRIVER"))
	{
		versionNumber = new StringBuffer();
		if (0 == jtd100.GetVersion(TD100Constants.VERSION_DRIVER, versionNumber))
	    {
	    StringBuffer stb = new StringBuffer();
	    stb.append(versionNumber.toString());
	    txtVersion.setText(stb.toString());
		} else
	    {
		    txtVersion.setText("");
		    ShowDialogBox("Exception occured while getting version information.\n Cannot get versions.");
	    }

	} else
	{
	    versionNumber = new StringBuffer();
		if (0 == jtd100.GetVersion(TD100Constants.VERSION_SDK, versionNumber)) {
			StringBuffer stb = new StringBuffer();
		    stb.append(versionNumber.toString());
		    txtVersion.setText(stb.toString());
	   }
	   else {
		    txtVersion.setText("");
		    ShowDialogBox("Exception occured while getting version information.\n Cannot get versions.");
	   }
	}
	    
	}

    /**
     * The Method to display a warning message box .The appropriate message is
     * set while calling this method.
     * 
     * @param String
     *            message
     */
    public void ShowDialogBox(String message)
    {
	JFrame showframe = new JFrame("");
	JOptionPane.showMessageDialog(showframe, message, "Java Sample", JOptionPane.INFORMATION_MESSAGE);
	showframe.setSize(200, 300);
	showframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Method to draw FaceBox when the chckbxShowFaceBox is selected depending
     * on the input parameters
     * 
     * @param g
     *            - The Graphics Object reference
     * @param image
     *            - The Image on which the rectangle needs to be drawn
     * @param resizedImageWidth
     *            - the inner box x-coordinate
     * @param resizedImageHeight
     *            - the inner box y-coordinate
     * @param xCoodinateOuter
     *            - the outer box x-coordinate
     * @param yCoodinateOuter
     *            - the outer box y-coordinate
     */
    public void DrawRectangle(Graphics g, Image image, long resizedImageWidth, long resizedImageHeight)
    {
	int xCoodinateInner = (int) (resizedImageWidth / 2 - inputWidthValue / 8);
	int yCoodinateInner = (int) (resizedImageHeight / 2 - inputHeightValue / 8);
	int xCoodinateOuter = (int) (resizedImageWidth / 2 - outputWidthValue / 8);
	int yCoodinateOuter = (int) (resizedImageHeight / 2 - outputHeightValue / 8);
	Graphics2D graphics = (Graphics2D) g;
	graphics.drawRect(xCoodinateOuter, yCoodinateOuter, (int) Math.ceil((outputWidthValue * 2) / 8), (int) Math.ceil((outputHeightValue * 2) / 8));
	graphics.setColor(Color.GREEN);
	graphics.drawRect(xCoodinateInner, yCoodinateInner, (int) Math.ceil((inputWidthValue * 2) / 8), (int) Math.ceil((inputHeightValue * 2) / 8));

    }

    /*
     * Method to Save the Selected Images in the local.
     */

	public void SaveImage() {
		// TODO Auto-generated method stub

		if (!chckbxSaveFaceImage.isSelected()
				&& (!chckbxSaveSceneImage.isSelected())
				&& (!chckbxSaveIrisImages.isSelected())) {

			ShowDialogBox("No Image Selected to Save");
		}

		else {

		
			/*
			 * Code to Save Iris Images
			 */

			if (true) {
				if (lblRightIrisCapture.getIcon() == null
						&& lblLeftIrisCapture.getIcon() == null) {
					ShowDialogBox("No Image to Save");
				} else {
					/*
					 * Saving Iris Recognition Images
					 */
					if (recognitionCreated == false
							&& rdbtnRecognition.isSelected()) {
						CreateFolder(RECOGNITION_FOLDER);
						recognitionCreated = true;
						irisRecognitionPath = folderStructure;
						SaveIrisImage(irisRecognitionPath, "R");
						ShowDialogBox("Iris image saved successfully.\n["
								+ irisRecognitionPath + "]");
						
						lblRightIrisCapture.setIcon(null);
					    lblLeftIrisCapture.setIcon(null);
					} else if (recognitionCreated == true
							&& rdbtnRecognition.isSelected()) {
						if (!(new File(irisRecognitionPath)).exists()) {
							CreateFolder(RECOGNITION_FOLDER);
							SaveIrisImage(irisRecognitionPath, "R");
							ShowDialogBox("Iris image saved successfully.\n["
									+ irisRecognitionPath + "]");
							
							lblRightIrisCapture.setIcon(null);
						    lblLeftIrisCapture.setIcon(null);
						} else {
							SaveIrisImage(irisRecognitionPath, "R");
							ShowDialogBox("Iris image saved successfully.\n["
									+ irisRecognitionPath + "]");
							
							lblRightIrisCapture.setIcon(null);
						    lblLeftIrisCapture.setIcon(null);
						}
					}

					/*
					 * Saving Iris Enrollment Images
					 */
					if (enrollmentCreated == false
							&& rdbtnEnrollment.isSelected()) {
						CreateFolder(ENROLLMENT_FOLDER);
						enrollmentCreated = true;
						irisEnrollmentPath = folderStructure;
						SaveIrisImage(irisEnrollmentPath, "E");
						ShowDialogBox("Iris image saved successfully.\n["
								+ irisEnrollmentPath + "]");
						
						lblRightIrisCapture.setIcon(null);
					    lblLeftIrisCapture.setIcon(null);
					} else if (enrollmentCreated == true
							&& rdbtnEnrollment.isSelected()) {

						if (!(new File(irisEnrollmentPath)).exists()) {
							CreateFolder(ENROLLMENT_FOLDER);
							SaveIrisImage(irisEnrollmentPath, "E");
							ShowDialogBox("Iris image saved successfully.\n["
									+ irisEnrollmentPath + "]");
							
							lblRightIrisCapture.setIcon(null);
						    lblLeftIrisCapture.setIcon(null);
						} else {
							SaveIrisImage(irisEnrollmentPath, "E");
							ShowDialogBox("Iris image saved successfully.\n["
									+ irisEnrollmentPath + "]");
							
							lblRightIrisCapture.setIcon(null);
						    lblLeftIrisCapture.setIcon(null);
						}
					}

					chckbxSaveIrisImages.setEnabled(false);
					chckbxSaveIrisImages.setSelected(false);
					if (chckbxSaveSceneImage.isEnabled()
							|| chckbxSaveFaceImage.isEnabled()) {
						btnSaveSelectedImages.setEnabled(true);
					} else {
						btnSaveSelectedImages.setEnabled(false);
					}
				}
			}
		}

	}

    /*
     * Method to Create Folders- Scene,Face,Enrollment,Recognition for saving Images respectively
     */
    
    public void CreateFolder(String name)
    {
        /* Fix for Elementool issue #626 */
    	JFileChooser fr = new JFileChooser();  
    	FileSystemView fw = fr.getFileSystemView();  
    	File documents = fw.getDefaultDirectory();
	//userHome = System.getProperty("user.home") + "\\My Documents\\";
	//File cam_file = new File(userHome, "iCAM TD100 SDK");
	File cam_file = new File("Fotos Olhos");
	boolean exists = cam_file.exists();
	
	    userHome =  "Fotos Olhos\\";

	
	File file = new File(userHome, name);

	folderStructure = userHome + name + "\\";
	
    }

    /*
     * Method to Save Iris Images which is internally called from SaveImages() when the mode is either Recognition or Enrollment
     */
    
    int ordem = 0;
	 public void SaveIrisImage(String path_name, String tmp)
    {
	try
	{
	    UUID temp_uid = UUID.randomUUID();
	    
	    if(lblRightIrisCapture.getIcon() != null ) {
	    	String rightEye = path_name + "//" + tmp + "_user_" + ordem + "_" + temp_uid + "_R.bmp";
	    	bmpFile.saveBitmap(rightEye, rightRawImageSave, 640, 480);
	    }
	    
	    if(lblLeftIrisCapture.getIcon() != null ) {
	    	String leftEye = path_name + "//" + tmp + "_user_" + ordem + "_" +temp_uid + "_L.bmp";
	    	bmpFile.saveBitmap(leftEye, leftRawImageSave, 640, 480);
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /*
     * Method to handle the Show Face Box Checkbox Select and Unselect && actionCommand.equalsIgnoreCase("CaptureFace")
     */
    public void chkFaceBox_OnClick()
    {

	if (chckbxShowFaceBox.isSelected())
	{

	    jtd100.ShowLCDFaceGuide(FACE_BOX_DISPLAY);

	} else
	{

	    jtd100.ShowLCDFaceGuide(FACE_BOX_NONE);

	}
    }
    

		



static boolean irisAuthentication (double comparison){
	if(comparison > 10000000){ //Por meio de testes, verificou-se que valores de comparação superiores a 10 milhões geram, no mais das vezes, autenticação
							   //inválida aproapriadamente (seja por não ser a mesma pessoa. 
		return false;		   //Falhando a autenticação, não é nem preciso fazer validação do estado da pupila; retorna-se de imediato um false.
	} else {				   //Se o valor da comparação for aceitável (inferior a 10 milhões), deve-se validar o tamanho da pupila.
		return true;		   //Chama-se o método responsável por validação de pupila.
	}
}

static void carControl (boolean check) {
	JOptionPane frame = new JOptionPane("Car Control");
	if(check) {JOptionPane.showMessageDialog(frame, "Carro Liberado");}
	else{JOptionPane.showMessageDialog(frame, "Carro não Liberado");}
	}

static boolean fitToDrive(int PupilDiameter, int IrisDiameter) {
	try {
	float a=IrisDiameter/PupilDiameter;
	if(a<40/100) {return false;}//iris contraída
	if(a>55/100){return false;}//iris dilatada
	return true; //if the drive is fit to drive
	}
	catch(Exception e){throw e;}
	}

	static void Sleepy(JOptionPane frame){ //diz baseado na tentativa de captura de foto, se pessoa está sonolenta ou não
	int contador=0;
	long irisResult = 0;
	Thread t = new Thread(new Runnable(){ 
		@Override	
		public void run(){long irisResult = jtd100.StartCapture(TD100Constants.IMG_MODE_IRIS_ENROLL);	}
		});
	//tenta capturar foto de íris
	
	JOptionPane.showMessageDialog(frame, "Monitorando Motorista");
		while (contador < 3){	
			t.start(); //tenta tirar foto
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// fica 1s tentando tirar foto
			t.interrupt(); //encerra a tentativa
			if(irisResult==0){contador = 0;} // Se deu certo, testa a proxima iteração
			if(irisResult!=0){contador++;} //Contador representa numero de vezes que não conseguiu tirar foto
		}
				JOptionPane.showMessageDialog(frame, "Motorista Sonolento");
		
		
		}
	
	}
	
	

/*
 * To Control the FaceBox Inner Width,Inner Height, Outer Width and Outer Height text box sizes to 4.
 */
class FixedSizeDocument extends PlainDocument
{
    private int max = 10;

    public FixedSizeDocument(int max)
    {
	this.max = max;
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
    {

	if (getLength() + str.length() > max)
	{

	    str = str.substring(0, max - getLength());
	}
	super.insertString(offs, str, a);
    }

}

