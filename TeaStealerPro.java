import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"Ramy"},version = 1.00, description = "The best Varrock Tea Stealer!", keywords = {"Tea","Stealer","Thiever"}, name = "TeaStealerPro")
public class TeaStealerPro extends Script implements PaintListener, MessageListener{

	final int Tea_ID = 1978;
	final int Tea_Seller_ID = 595;
	final int Tea_Stall_ID = 635;

	//XP
	int StartXP = 0;
	int GainedXP = 0;
	int XPPH = 0;
	int XPTNL = 0;
	
	//Teas
	int StolenTeas = 0;
	int TeasPH = 0;
	int XPPT = 1;
	
	//Level
	int StartLevel = 0;
	int LevelsGained = 0;
	int CurrentLevel = 0;

	//RunTime
	long startTime = 0;
	long millis = 0;
	long hours = 0;
	long minutes = 0;
	long seconds = 0;
	
	//Status
	String status = "";
	
	public enum AntiBan{
		moveRandomly , HoverSkill , Tabs
	}
	public enum Status{
		Drop, Steal , Walk , Wait
	}
	public boolean onStart(){
		startTime = System.currentTimeMillis();
		StartLevel = skills.getCurrentLevel(Skills.THIEVING);
		StartXP = skills.getCurrentExp(Skills.THIEVING);
		
		mouse.setSpeed(random(8,10));
		
		return true;
	}
	@Override
	public int loop() {
		try {
		switch(getStatus()){
		case Drop:
			inventory.dropAll(Tea_ID);
				sleep(50,100);
		case Steal:
			RSObject stall = objects.getNearest(Tea_Stall_ID);
			if(stall != null){
				stall.interact("Steal-from");
				sleep(400,1200);
				int x = random(0,5);
				if(x > 3){
					AntiBan(AntiBan.HoverSkill,random(400,1200));
				}else if (x > 1){
					AntiBan(AntiBan.Tabs,random(200,800));
				}else{
					AntiBan(AntiBan.moveRandomly,random(200,500));
				}
			}
		case Walk:
			if(!getMyPlayer().getLocation().equals(new RSTile(3268,3410))){
			walking.walkTileMM(new RSTile(3268,3410));
			sleep(200,300);
			while(getMyPlayer().isMoving()){
				sleep(200,300);
			}
			}
		case Wait:
			AntiBan(AntiBan.moveRandomly,random(200,400));
			sleep(200,100);
		}
		} catch (Exception e) {
			log("Error: " + e);
		}
		return 0;
	}
	private Status getStatus() {
		RSNPC a = npcs.getNearest(Tea_Seller_ID);
		RSObject stall = objects.getNearest(Tea_Stall_ID);
		RSItem tea = inventory.getItem(Tea_ID);

		if(stall != null){
			if(a != null && a.getMessage() != null){
			if(a.getMessage().toLowerCase().contains("hey! get your hands off")){
				status = "Walking to " + stall.getName() + "..";
				return Status.Walk;
			}}
			status = "Stealing..";
			return Status.Steal;
		}else if(tea != null){
			status = "Droping..";
			return Status.Drop;
		}
		return Status.Wait;
	}
	public void onFinish(){
		log(Color.blue,"Thanks for using TeaStealerPro!");
	}
	public void AntiBan(AntiBan a, int i){
		switch (a){
		case HoverSkill:
			status = "AntiBan(Hovering)..";
			skills.doHover(Skills.INTERFACE_THIEVING);
			sleep(i);
		case Tabs:
			status = "AntiBan(Tabs)..";
			switch(random(0,4)){
			case 0:
				game.openTab(Game.Tab.ATTACK);
				sleep(i);
				if(random(0,2) == 0){
				break;
				}
			case 1:
				game.openTab(Game.Tab.EMOTES);
				sleep(i);
				break;
			case 2:
				game.openTab(Game.Tab.FRIENDS);
				sleep(i);
				if(random(0,2) == 0){
				break;
				}
			case 3:
				game.openTab(Game.Tab.OPTIONS);
				sleep(i);
				break;
			}
			
		case moveRandomly:
			status = "AntiBan(Camera)";
			camera.moveRandomly(i);
		}
	}
    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
        	log.severe("Error Loading Image!");
            return null;
        }
    }

    private final Color color1 = new Color(0, 153, 153, 156);
    private final Color color2 = new Color(0, 0, 0);
    private final Color color3 = new Color(255, 0, 51);
    private final Color color4 = new Color(0, 0, 255);
    private final Color color5 = new Color(255, 255, 0);

    private final BasicStroke stroke1 = new BasicStroke(2);

    private final Font font1 = new Font("Arial", 0, 18);
    private final Font font2 = new Font("Arial", 2, 15);

    private final Image img1 = getImage("http://www.runescape.com/img/main/kbase/items/food/drink/cup_tea.gif");

    public void onRepaint(Graphics g1) {
        Graphics2D g = (Graphics2D)g1;
        
		millis = System.currentTimeMillis() - startTime;
		hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		seconds = millis / 1000;
		
		
		GainedXP = skills.getCurrentExp(Skills.THIEVING) - StartXP;
		XPTNL = skills.getExpToNextLevel(Skills.THIEVING);
		TeasPH = (int) ((StolenTeas) * 3600000D / (System.currentTimeMillis() - startTime));;
		XPPH = (int) ((GainedXP) * 3600000D / (System.currentTimeMillis() - startTime));
		CurrentLevel = skills.getCurrentLevel(Skills.THIEVING);
		LevelsGained = skills.getCurrentLevel(Skills.THIEVING) - CurrentLevel;
		
		long runTime = System.currentTimeMillis() - startTime;
		long ttl = (long) (XPTNL * ((double) runTime / GainedXP));
		int ttlH = (int) (ttl / 3600000D);
		int ttlM = (int) ((ttl % 3600000D) / 60000D);
		int ttlS = (int) (((ttl % 3600000D) % 60000D) / 1000D);
		
		g.setColor(color1);
        g.fillRoundRect(547, 206, 190, 259, 16, 16);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRoundRect(547, 206, 190, 259, 16, 16);
        g.setFont(font1);
        g.setColor(color3);
        g.drawString("TeaStealerPro", 581, 227);
        g.setFont(font2);
        g.setColor(color4);
        g.drawString("Gained Xp: " + GainedXP, 557, 264);
        g.drawString("Xp Per Hours: " + XPPH, 557, 286);
        g.drawString("XP TNL: " + XPTNL + ".", 557, 308);
        g.drawString("Time TNL: " + ttlH + ":" + ttlM + ":" + ttlS + ".", 557, 330);
        g.drawString("Stolen Teas: " + StolenTeas + ".", 557, 352);
        g.drawString("Teas Per Hour: " + TeasPH + ".", 557, 374);
        g.drawString("Level: " + CurrentLevel + "(" + LevelsGained + ").", 557, 396);
        g.drawString("Status: " + status + ".", 557, 418);
        g.setColor(color5);
        g.drawString("RunTime: " + hours + ":" + minutes + ":" + seconds + ".", 557, 450);
        g.drawImage(img1, 703, 207, null);
    }
	@Override
	public void messageReceived(MessageEvent e) {
		String m = e.getMessage();
		if(m.toLowerCase().contains("you steal a cup of tea")){
			StolenTeas++;
		}
	}

}