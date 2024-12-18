package jdbc_Assignment;

import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;

@SuppressWarnings("serial")
public class JDBCMainWindowContent extends JInternalFrame implements ActionListener{

	String cmd = null;

	// DB Connectivity Attributes
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;

	private Container content;

	private JPanel detailsPanel;
	private JPanel exportButtonPanel;
	//private JPanel exportConceptDataPanel;
	private JScrollPane dbContentsPanel;

	private Border lineBorder;

	
	private JLabel Mon_NameLabel = new JLabel("Monster_Name:                 ");
	private JLabel Mon_DmgLabel = new JLabel("Monster_Damage_Scale:               ");
	private JLabel Mon_ThtLabel = new JLabel("Monster_threat_level:      ");
	private JLabel FireLabel = new JLabel("Fire_Res:        ");
	private JLabel WaterLabel = new JLabel("Water_Res:                 ");
	private JLabel ThunderLabel = new JLabel("Thunder_Res:               ");
	private JLabel DragonLabel = new JLabel("Dragon_Res:      ");
	private JLabel IceLabel = new JLabel("Ice_Res:      ");

	
	private JTextField Mon_NameTF = new JTextField(10);
	private JTextField Mon_DmgTF = new JTextField(10);
	private JTextField Mon_ThtTF = new JTextField(10);
	private JTextField FireTF = new JTextField(10);
	private JTextField WaterTF = new JTextField(10);
	private JTextField ThunderTF = new JTextField(10);
	private JTextField DragonTF = new JTextField(10);
	private JTextField IceTF = new JTextField(10);
	
	private JTextField Mon_TypeTF = new JTextField(10);
	private JTextField DescTF = new JTextField(10);
	

	private static QueryTableModel TableModel = new QueryTableModel();
	//Add the models to JTabels
	private JTable TableofDBContents = new JTable(TableModel);
	//Buttons for inserting, and updating members
	//also a clear button to clear details panel
	private JButton updateButton = new JButton("Update");
	private JButton insertButton = new JButton("Insert");
	private JButton exportButton  = new JButton("Export");
	private JButton deleteButton  = new JButton("Delete");
	private JButton clearButton  = new JButton("Clear");

	private JButton NumMonster = new JButton("NumMonstersInEachGroup:");
	private JButton ListAllMonsters  = new JButton("ListAllMonsters");
	private JButton ListAllAilments  = new JButton("ListAllAilments");
	private JButton AvgMonsterThreat  = new JButton("AvgMonsterThreat");



	public JDBCMainWindowContent( String aTitle)
	{	
		//setting up the GUI
		super(aTitle, false,false,false,false);
		setEnabled(true);

		initiate_db_conn();
		//add the 'main' panel to the Internal Frame
		content=getContentPane();
		content.setLayout(null);
		content.setBackground(Color.lightGray);
		lineBorder = BorderFactory.createEtchedBorder(15, Color.red, Color.black);

		//setup details panel and add the components to it
		detailsPanel=new JPanel();
		detailsPanel.setLayout(new GridLayout(11,2));
		detailsPanel.setBackground(Color.lightGray);
		detailsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "CRUD Actions"));

		
		detailsPanel.add(Mon_NameLabel);
		detailsPanel.add(Mon_DmgLabel);
		detailsPanel.add(Mon_ThtLabel);		
		detailsPanel.add(FireLabel);	
		detailsPanel.add(WaterLabel);		
		detailsPanel.add(ThunderLabel);
		detailsPanel.add(DragonLabel);
		detailsPanel.add(IceLabel);
		
		
		
		detailsPanel.add(Mon_NameTF);
		detailsPanel.add(Mon_DmgTF);
		detailsPanel.add(Mon_ThtTF);
		detailsPanel.add(FireTF);
		detailsPanel.add(WaterTF);
		detailsPanel.add(ThunderTF);
		detailsPanel.add(DragonTF);
		detailsPanel.add(IceTF);
		

		//setup details panel and add the components to it
		exportButtonPanel=new JPanel();
		exportButtonPanel.setLayout(new GridLayout(3,2));
		exportButtonPanel.setBackground(Color.lightGray);
		exportButtonPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Export Data"));
		
		exportButtonPanel.add(NumMonster);
		exportButtonPanel.add(ListAllMonsters);
		exportButtonPanel.add(ListAllAilments);
		exportButtonPanel.add(AvgMonsterThreat);

		exportButtonPanel.setSize(500, 200);
		exportButtonPanel.setLocation(3, 300);
		content.add(exportButtonPanel);

		insertButton.setSize(100, 30);
		updateButton.setSize(100, 30);
		exportButton.setSize (100, 30);
		deleteButton.setSize (100, 30);
		clearButton.setSize (100, 30);

		insertButton.setLocation(370, 10);
		updateButton.setLocation(370, 110);
		exportButton.setLocation (370, 160);
		deleteButton.setLocation (370, 60);
		clearButton.setLocation (370, 210);

		insertButton.addActionListener(this);
		updateButton.addActionListener(this);
		exportButton.addActionListener(this);
		deleteButton.addActionListener(this);
		clearButton.addActionListener(this);

		this.ListAllMonsters.addActionListener(this);
		this.NumMonster.addActionListener(this);
		this.ListAllAilments.addActionListener(this);
		this.AvgMonsterThreat.addActionListener(this);


		content.add(insertButton);
		content.add(updateButton);
		content.add(exportButton);
		content.add(deleteButton);
		content.add(clearButton);


		TableofDBContents.setPreferredScrollableViewportSize(new Dimension(900, 300));

		dbContentsPanel=new JScrollPane(TableofDBContents,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dbContentsPanel.setBackground(Color.lightGray);
		dbContentsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder,"Database Content"));

		detailsPanel.setSize(360, 300);
		detailsPanel.setLocation(3,0);
		dbContentsPanel.setSize(700, 300);
		dbContentsPanel.setLocation(477, 0);

		content.add(detailsPanel);
		content.add(dbContentsPanel);

		setSize(982,645);
		setVisible(true);

		TableModel.refreshFromDB(stmt);
	}

	public void initiate_db_conn()
	{
		try
		{
			// Load the JConnector Driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Specify the DB Name
            String url="jdbc:mysql://localhost:3306/db4_mh";
			
			// Connect to DB using DB URL, Username and password
			con = DriverManager.getConnection(url, "root", "");
			
			//Create a generic statement which is passed to the TestInternalFrame1
			stmt = con.createStatement();
		}
		catch(Exception e)
		{
			System.out.println("Error: Failed to connect to database\n"+e.getMessage());
		}
	}

	//event handling 
	public void actionPerformed(ActionEvent e)
	{
		Object target=e.getSource();
		if (target == clearButton)
		{
			
			Mon_NameTF.setText("");
			Mon_DmgTF.setText("");
			Mon_ThtTF.setText("");
			FireTF.setText("");
			WaterTF.setText("");
			ThunderTF.setText("");
			DragonTF.setText("");
			IceTF.setText("");
	
			Mon_TypeTF.setText("");
			DescTF.setText("");
			
		}

		if (target == insertButton)
		{		 
			try
			{
			
				String updateMon = "INSERT INTO monster VALUES('"
					    + Mon_NameTF.getText() + "','"
					    + Mon_TypeTF.getText() + "','"
					    + DescTF.getText() + "')";

				String updateStats = "INSERT INTO monster_stats VALUES('"
				    + Mon_NameTF.getText() + "','"
				    + Mon_DmgTF.getText() + "',"
				    + Mon_ThtTF.getText() + ")";

				String updateElements = "INSERT INTO elements VALUES('"
				    + Mon_NameTF.getText() + "',"
				    + FireTF.getText() + ","
				    + WaterTF.getText() + ","
				    + ThunderTF.getText() + ","
				    + DragonTF.getText() + ","
				    + IceTF.getText() + ")";
				
				stmt.executeUpdate(updateMon);
				stmt.executeUpdate(updateStats);
				stmt.executeUpdate(updateElements);

			}
			catch (SQLException sqle)
			{
				System.err.println("Error with  insert:\n"+sqle.toString());
			}
			finally
			{
				TableModel.refreshFromDB(stmt);
			}
		}
		
		/////////////////////////////////////////////////////////////////////////////////////
		//Delete operation
		///////////////////////////////////////////////////////////////////////////////////
		
		if (target == deleteButton)
		{		 
			try
			{
				//String DeleteMon = "delete from monster where monster_name = '"
					//	+ Mon_NameTF.getText() + "'";
				
				
				String DeleteStats = "delete from monster_stats where monster_name = '"
						+ Mon_NameTF.getText() + "'";
				
				String DeleteElements = "delete from elements where monster_name = '"
						+ Mon_NameTF.getText() + "'";
						
				String DeleteMon = "delete from monster where monster_name = '"
						+ Mon_NameTF.getText() + "'";
			
				
				stmt.executeUpdate(DeleteStats);
				stmt.executeUpdate(DeleteElements);
				stmt.executeUpdate(DeleteMon);
			}
			catch (SQLException sqle)
			{
				System.err.println("Error with deletion:\n"+sqle.toString());
			}
			finally
			{
				TableModel.refreshFromDB(stmt);
			}
		}
		
		/////////////////////////////////////////////////////////////////////////////////////
		//Update operation
		///////////////////////////////////////////////////////////////////////////////////
		
		if (target == updateButton)
		{		 
			try
			{	
				String updateMon = "UPDATE monster SET " +
		                   "Monster_type='" + Mon_TypeTF.getText() + "', " +
		                   "Monster_desc='" + DescTF.getText() + "' " + 
		                   "WHERE Monster_name='" + Mon_NameTF.getText() + "'";
				
				String updateStats = "UPDATE monster_stats SET " +
					    "Monster_dmg = '" + Mon_DmgTF.getText() + "', " +
					    "Monster_threat = '" + Mon_ThtTF.getText() + "' " +
					    "WHERE Monster_name = '" + Mon_NameTF.getText() + "'"; 

				String updateElements = "UPDATE elements SET " +
					    "Fire = '" + FireTF.getText() + "', " +
					    "Water = '" + WaterTF.getText() + "', " + 
					    "Thunder = '" + ThunderTF.getText() + "', " + 
					    "Dragon = '" + DragonTF.getText() + "', " +  
					    "Ice = '" + IceTF.getText() + "' " +
					    "WHERE Monster_name = '" + Mon_NameTF.getText() + "'";
		
				stmt.executeUpdate(updateMon);
				stmt.executeUpdate(updateStats);
				stmt.executeUpdate(updateElements);
						
			}
			catch (SQLException sqle)
			{
				System.err.println("Error with update:\n"+sqle.toString());
			}
			finally
			{
				TableModel.refreshFromDB(stmt);
			}
		}
		/////////////////////////////////////////////////////////////////////////////////////
		//I have only added functionality of 2 of the button on the lower right of the template
		///////////////////////////////////////////////////////////////////////////////////

		if(target == this.ListAllMonsters){

			cmd = "select * from Monster;";

			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}

		if(target == this.ListAllAilments){
	
			cmd = "select * from Ailments;";
	
			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}
	
		}
		
		if(target == this.AvgMonsterThreat){

			cmd = "SELECT m.Monster_type, " +
                    "COUNT(*) as monster_count, " +
                    "AVG(ms.Monster_threat) as avg_threat_level " +
                    "FROM Monster m " +
                    "JOIN Monster_Stats ms ON m.Monster_name = ms.Monster_name " +
                    "GROUP BY m.Monster_type " +
                    "ORDER BY avg_threat_level DESC";

			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}

		
		if(target == this.NumMonster){

			 cmd = "SELECT Monster_type, " +
                     "COUNT(*) as monster_count " +
                     "FROM Monster " +
                     "GROUP BY Monster_type";
			 
			System.out.println(cmd);
			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}
	}
	///////////////////////////////////////////////////////////////////////////

	private void writeToFile(ResultSet rs){
		try{
			System.out.println("In writeToFile");
			FileWriter outputFile = new FileWriter("MyOutput.csv");
			PrintWriter printWriter = new PrintWriter(outputFile);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();

			for(int i=0;i<numColumns;i++){
				printWriter.print(rsmd.getColumnLabel(i+1)+",");
			}
			printWriter.print("\n");
			while(rs.next()){
				for(int i=0;i<numColumns;i++){
					printWriter.print(rs.getString(i+1)+",");
				}
				printWriter.print("\n");
				printWriter.flush();
			}
			printWriter.close();
		}
		catch(Exception e){e.printStackTrace();}
	}
}

	///////////////////////////////////////////////////////////////////////////
