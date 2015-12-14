package PantheraPackage;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

public class PantheraClass {

	private JFrame mainMap = new JFrame();
	private Shape[] poly = new Shape[16];
	JPanel p;
	String chkclick;
	String url;
	Properties props;
	Container content;
	String regionid[] = new String[16];
	final JCheckBox chkShowLions = new JCheckBox("Show Lions and Pond in selected region");

	public PantheraClass() throws SQLException {
		initComponents();
	}

	private void initComponents() throws SQLException {

		url = "jdbc:oracle:thin:@localhost:1521/xe";
		props = new Properties();
		props.setProperty("user", "harishsn");
		props.setProperty("password", "sofgold");

		Connection conn = DriverManager.getConnection(url, props);

		String sql = "select region_id,region_shape as shp from region p";

		PreparedStatement preStatement = conn.prepareStatement(sql);

		ResultSet result = preStatement.executeQuery();
		mainMap.setResizable(false);

		mainMap.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		int i = 0;

		while (result.next()) {
			regionid[i] = result.getObject(1).toString();
			STRUCT st = (oracle.sql.STRUCT) result.getObject(2);
			// convert STRUCT into geometry
			JGeometry j_geom = JGeometry.load(st);
			poly[i] = j_geom.createShape();

			i++;
		}

		Connection con = DriverManager.getConnection(url, props);

		String sql1 = "select pond_shape as shp from pond p";

		PreparedStatement pre = conn.prepareStatement(sql1);

		ResultSet result1 = pre.executeQuery();
		i = 0;
		double centerX[] = new double[8];
		double centerY[] = new double[8];
		int radius = 15;
		while (result1.next()) {

			STRUCT st1 = (oracle.sql.STRUCT) result1.getObject(1);
			// Convert STRUCT into geometry
			JGeometry j_geom = JGeometry.load(st1);
			centerX[i] = j_geom.getFirstPoint()[0] - 2 * radius;
			centerY[i] = j_geom.getFirstPoint()[1] - radius;

			i++;
		}

		String sql2 = "select a.ambulancearea_shape as shp from ambulancearea a";

		PreparedStatement pre1 = conn.prepareStatement(sql2);

		ResultSet result2 = pre1.executeQuery();
		i = 0;
		double acenterX[] = new double[5];
		double acenterY[] = new double[5];
		int aradius = 90;
		while (result2.next()) {

			STRUCT st2 = (oracle.sql.STRUCT) result2.getObject(1);
			// Convert STRUCT into geometry
			JGeometry j_geom = JGeometry.load(st2);
			acenterX[i] = j_geom.getFirstPoint()[0] - aradius;
			acenterY[i] = j_geom.getFirstPoint()[1];

			i++;
		}

		String sql3 = "select l.lion_shape as shp from lion l";

		PreparedStatement pre2 = conn.prepareStatement(sql3);

		ResultSet result3 = pre2.executeQuery();
		i = 0;
		double[] pointX = new double[14];
		double[] pointY = new double[14];
		while (result3.next()) {

			STRUCT st3 = (oracle.sql.STRUCT) result3.getObject(1);
			// Convert STRUCT into geometry
			JGeometry j_geom = JGeometry.load(st3);
			pointX[i] = j_geom.getFirstPoint()[0] - 2 * 4;
			pointY[i] = j_geom.getFirstPoint()[1] - 4;
			i++;

		}

		drawPolygon(poly, centerX, centerY, radius, acenterX, acenterY, aradius, pointX, pointY, null, null, null,
				null);

	}

	private void setClick(String value) {
		chkclick = value;
	}

	private void drawPolygon(Shape[] poly, double[] centerX, double[] centerY, int radius, double[] acenterX,
			double[] acenterY, int aradius, double[] pointX, double[] pointY, double[] pointXPond, double[] pointYPond,
			double[] pointXLion, double[] pointYLion) {
		p = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D test = (Graphics2D) g;
				super.paintComponent(test);
				for (Shape pol : poly) {
					test.setColor(Color.WHITE);
					test.fill(pol);
				}
				for (Shape pol : poly) {
					test.setColor(Color.BLACK);
					test.draw(pol);
				}

				for (int j = 0; j < centerX.length; j++) {
					if (pointXPond == null && pointYPond == null) {
						test.setColor(Color.BLACK);
						test.drawOval((int) centerX[j], (int) centerY[j], 2 * radius, 2 * radius);
						test.setColor(Color.BLUE);
						test.fillOval((int) centerX[j], (int) centerY[j], 2 * radius, 2 * radius);
					} else {
						for (int i = 0; i < pointXPond.length; i++)
							if (!(centerX[j] == pointXPond[i] && centerY[j] == pointYPond[i])) {
								test.setColor(Color.BLACK);
								test.drawOval((int) centerX[j], (int) centerY[j], 2 * radius, 2 * radius);
								test.setColor(Color.BLUE);
								test.fillOval((int) centerX[j], (int) centerY[j], 2 * radius, 2 * radius);
							}
					}
				}

				for (int l = 0; l < pointX.length; l++) {
					if (pointXLion == null && pointYLion == null) {
						test.setColor(Color.GREEN);
						test.drawOval((int) pointX[l], (int) pointY[l], 2 * 4, 2 * 4);
						test.setColor(Color.GREEN);
						test.fillOval((int) pointX[l], (int) pointY[l], 2 * 4, 2 * 4);
					} else {
						for (int i = 0; i < pointXLion.length; i++)
							if (!(pointX[l] == pointXLion[i] && pointY[l] == pointYLion[i])) {
								test.setColor(Color.GREEN);
								test.drawOval((int) pointX[l], (int) pointY[l], 2 * 4, 2 * 4);
								test.setColor(Color.GREEN);
								test.fillOval((int) pointX[l], (int) pointY[l], 2 * 4, 2 * 4);
							}
					}
				}

				if (pointXLion != null && pointYLion != null) {
					for (int i = 0; i < pointXLion.length; i++) {
						test.setColor(Color.RED);
						test.drawOval((int) pointXLion[i], (int) pointYLion[i], 2 * 4, 2 * 4);
						test.setColor(Color.RED);
						test.fillOval((int) pointXLion[i], (int) pointYLion[i], 2 * 4, 2 * 4);
					}
				}

				if (pointXPond != null && pointYPond != null) {
					for (int i = 0; i < pointXPond.length; i++) {
						test.setColor(Color.RED);
						test.drawOval((int) pointXPond[i], (int) pointYPond[i], 2 * radius, 2 * radius);
						test.setColor(Color.RED);
						test.fillOval((int) pointXPond[i], (int) pointYPond[i], 2 * radius, 2 * radius);
					}
				}
				if (chkclick == "checked") {
					chkShowLions.setSelected(true);
				}

				MouseAdapter ma = new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent me) {
						super.mouseClicked(me);
						if (chkclick == "checked") {
							int coordinateX = me.getX();
							int coordinateY = me.getY();
							try {

								Connection con = DriverManager.getConnection(url, props);

								String regs[] = new String[1];

								for (int i = 0; i < 16; i++) {
									if (poly[i].contains((int) coordinateX, (int) coordinateY)) {
										regs[0] = regionid[i];
									}
								}

								if (regs[0] != null) {

									String sqllioncount = "select count(lion_shape) from (select lion_shape from lion where lion_id in (select distinct l.lion_id from lion l,region e where SDO_RELATE(l.lion_shape,e.REGION_SHAPE,'mask=INSIDE')='TRUE'and e.REGION_ID='"
											+ regs[0] + "'))";
									PreparedStatement precountlion = con.prepareStatement(sqllioncount);
									ResultSet resultlioncount = precountlion.executeQuery();
									int lioncount = 0;
									while (resultlioncount.next()) {
										lioncount = ((Number) resultlioncount.getObject(1)).intValue();
									}

									String sqllion = "select lion_shape from lion where lion_id in (select distinct l.lion_id from lion l,region e where SDO_RELATE(l.lion_shape,e.REGION_SHAPE,'mask=INSIDE')='TRUE'and e.REGION_ID='"
											+ regs[0] + "')";
									PreparedStatement prelion = con.prepareStatement(sqllion);
									ResultSet resultlion = prelion.executeQuery();

									double pointarrX[] = null;
									double pointarrY[] = null;
									if (lioncount != 0) {
										pointarrX = new double[lioncount];
										pointarrY = new double[lioncount];
									}
									int i = 0;
									while (resultlion.next()) {
										STRUCT stlion = (oracle.sql.STRUCT) resultlion.getObject(1);
										JGeometry j_geom = JGeometry.load(stlion);

										pointarrX[i] = j_geom.getFirstPoint()[0] - 2 * 4;
										pointarrY[i] = j_geom.getFirstPoint()[1] - 4;
										i++;
									}

									String sqlpondcount = "select count(pond_shape) from (select pond_shape from pond where pond_id in (select distinct p.pond_id from pond p,region e where SDO_RELATE(p.pond_shape,e.REGION_SHAPE,'mask=INSIDE')='TRUE'and e.REGION_ID='"
											+ regs[0] + "'))";
									PreparedStatement precountpond = con.prepareStatement(sqlpondcount);
									ResultSet resultpondcount = precountpond.executeQuery();
									int pondcount = 0;
									while (resultpondcount.next()) {
										pondcount = ((Number) resultpondcount.getObject(1)).intValue();
									}

									String sqlpond = "select pond_shape from pond where pond_id in (select distinct p.pond_id from pond p,region e where SDO_RELATE(p.pond_shape,e.REGION_SHAPE,'mask=INSIDE')='TRUE'and e.REGION_ID='"
											+ regs[0] + "')";
									PreparedStatement prepond = con.prepareStatement(sqlpond);

									ResultSet resultpond = prepond.executeQuery();
									i = 0;
									double pondarrX[] = null;
									double pondarrY[] = null;

									if (pondcount != 0) {
										pondarrX = new double[pondcount];
										pondarrY = new double[pondcount];
									}
									while (resultpond.next()) {
										STRUCT stpond = (oracle.sql.STRUCT) resultpond.getObject(1);
										JGeometry j_geom = JGeometry.load(stpond);

										pondarrX[i] = j_geom.getFirstPoint()[0] - 2 * radius;
										pondarrY[i] = j_geom.getFirstPoint()[1] - radius;
										i++;
									}

									drawPolygon(poly, centerX, centerY, radius, acenterX, acenterY, aradius, pointX,
											pointY, pondarrX, pondarrY, pointarrX, pointarrY);

								} else {
									drawPolygon(poly, centerX, centerY, radius, acenterX, acenterY, aradius, pointX,
											pointY, null, null, null, null);
								}

							} catch (SQLException ex) {

							}
						}
					}
				};
				this.addMouseListener(ma);
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(700, 700);

			}

		};

		chkShowLions.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					setClick("checked");
				} else {
					setClick("setClick");
					drawPolygon(poly, centerX, centerY, radius, acenterX, acenterY, aradius, pointX, pointY, null, null,
							null, null);
				}

			}
		});

		p.setBorder(new EmptyBorder(100, 100, 100, 100));
		if (pointXLion != null || pointXPond != null) {
			chkShowLions.setSelected(true);
		}

		content = mainMap.getContentPane();
		content.setLayout(new GridBagLayout());
		content.removeAll();
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;

		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.PAGE_START;

		content.add(p, c);
		c.insets = new Insets(5, 5, 5, 5);
		c.gridy = 1;

		content.add(chkShowLions, c);
		mainMap.add(p);

		mainMap.pack();

		Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainMap.setLocation((scrnSize.width / 2) - 500, (scrnSize.height / 2) - 500);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error" + e);
		}

		mainMap.setVisible(true);

	}

	public static void main(String[] args) throws SQLException {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new PantheraClass();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
