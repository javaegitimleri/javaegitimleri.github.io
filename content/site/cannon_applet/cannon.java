// Parabolic trajectory prepared by Kenan Sevindik & Tugrul Yilmaz
Parabolic trajectory prepared by Kenan Sevindik & Tugrul Yilmaz ...Parabolic trajectory prepared by Kenan Sevindik & Tugrul Yilmaz 

import java.awt.*;
import java.net.*;
import java.applet.*;
//import java.lang.*;
import java.lang.Math;
import java.lang.Integer;

public class cannon  extends Applet {

       Control_Panel panel;
       Panel p2,p3,p4,p5;

       public TextField ang;
       public TextField vel;
       public TextField grav;
       public TextField wind;
 
       Scrollbar s_ang;
       Scrollbar s_vel;
       Scrollbar s_grav;
       Scrollbar s_wind;

       public boolean work=false;
  

       public void init(){
             
              this.setLayout(new BorderLayout());
              panel=new Control_Panel(this);
              add("Center",panel);

              panel.init();
              
              p2=new Panel();
              add("South",p2);
              p2.setLayout(new BorderLayout());
              
              p3=new Panel();
              p2.add("Center",p3);
              
              p3.add(new Button("Shoot"));
              p3.add(new Button("More Ammo"));

              p3.add(new Label("Ang:"));
              p3.add(ang=new TextField("60", 4));
              ang.setEditable(false);
              
              p3.add(new Label("Vel:"));
              p3.add(vel=new TextField("15",4));
              vel.setEditable(false);

              p3.add(new Label("Grav:"));
              p3.add(grav=new TextField("9.8",4));
              grav.setEditable(false);

              p3.add(new Label("Wind"));
              p3.add(wind=new TextField("0",4));
              wind.setEditable(false);

              p4=new Panel();
              p2.add("South",p4);
              p4.setLayout(new GridLayout(4,1));

              p4.add(s_ang=new Scrollbar(Scrollbar.HORIZONTAL));
              s_ang.setValues(60,10,1,90);

              p4.add(s_vel=new Scrollbar(Scrollbar.HORIZONTAL));
              s_vel.setValues(10,2,1,30);

              p4.add(s_grav=new Scrollbar(Scrollbar.HORIZONTAL));
              s_grav.setValues(98,10,40,200);

              p4.add(s_wind=new Scrollbar(Scrollbar.HORIZONTAL));
              s_wind.setValues(0,1,-10,10);

//              p5=new Panel();
//              p2.add("North",p5);
//              p5.add(new Button("Shoot"));
//              p5.add(new Button("More Ammo"));
     } // end of init
     
     public boolean handleEvent(Event event){
            if(event.target==s_ang)
              panel.changeAngle(((Integer)event.arg).intValue());
            else if(event.target==s_vel)
              panel.changeVelocity(((Integer)event.arg).intValue());
            else if(event.target==s_grav)
              panel.changeGravity(((Integer)event.arg).intValue());
            else if(event.target==s_wind)
              panel.changeWindage(((Integer)event.arg).intValue());
            else if("Shoot".equals(event.arg)){
              work=true;
              panel.shootShot();
            }
            else if("More Ammo".equals(event.arg))
              panel.more();
            else
              return super.handleEvent(event);
            return true;
    }  // end of handleEvent
}      // end of cannon class

class Control_Panel extends Panel{
      
      cannon cannon_applet;
      double dx, dy, theta, velocity, gravity, windage;
      int dxi,dyi,dxo,dyo,dxi_o,dyi_o,xo,yo;
      int xmax, ymax, numshots;
      Color back_ground;
      double x, y;
      int xi,yi;
      double rads;
      Image can, shot, targ, targ2,bang1;

      
     Control_Panel(cannon cannon_applet){
            this.cannon_applet=cannon_applet;
     }
 
     public void more(){
            numshots = 4;
            repaint();
     }
 
     public void shootShot(){
            if((numshots>0) && (cannon_applet.work==true)){
              numshots--;
              repaint();
            }
     }
     public void loadImages(){
            can = cannon_applet.getImage(cannon_applet.getCodeBase(), "images/cannon.gif");
            shot = cannon_applet.getImage(cannon_applet.getCodeBase(), "images/ammo.gif");
            targ = cannon_applet.getImage(cannon_applet.getCodeBase(), "images/target.gif");
            targ2 = cannon_applet.getImage(cannon_applet.getCodeBase(), "images/target2.gif");
            bang1 = cannon_applet.getImage(cannon_applet.getCodeBase(), "images/bang1.gif");
    }
    public void init(){
           loadImages();
           numshots = 4;
           rads = 57.29577866f;
           xmax = 550;
           ymax = 280;
           this.resize(xmax,ymax+20);
           theta = 60;               // degrees
           theta /= rads;            // -> radians
           velocity = 3;             // m/s x 10^-1
           gravity = .098f;          // m/s^2 x 10^-2
           windage = 0;
   }
 
   public void changeAngle(int val){
          Integer temp = new Integer(val);
          theta = (double)val;              // degrees
          theta /= rads;                    // -> radians
          cannon_applet.ang.setText(temp.toString());
   }
 
   public void changeVelocity(int val){
          Integer temp = new Integer(val);
          velocity = (double)val / 5;
          cannon_applet.vel.setText(temp.toString());
   }
 
   public void changeGravity(int val){
          Float temp = new Float((float)val / 10);
          gravity = (double)val / 1000;
          cannon_applet.grav.setText(temp.toString());
   }
 
   public void changeWindage(int val){
          Integer temp = new Integer(val);
          windage = (double)val / 200;
          cannon_applet.wind.setText(temp.toString());
   }
   
   public void paint(Graphics g){
          int[] tracks_x=new int[90];
          int[] tracks_y=new int[90];
          int[] tracks_dx=new int[90];
          int[] tracks_dy=new int[90];
          Rectangle dim;
          dim=this.bounds();
          int track_number=0,counter=0;
          int track_p;
//          this.setBackground(Color.lightgray);
          back_ground = this.getBackground();
          g.setColor(Color.black);
          g.drawLine(65,ymax-55,dim.width-1,ymax-55);
          g.drawLine(65,ymax-55,65,1);
          if(can == null){ return; }

          //##### Draw images
          g.drawImage(can, 20, ymax - can.getHeight(this), this);
          g.drawImage(targ, xmax - (targ.getWidth(this) + 10), ymax - targ.getHeight(this)-10, this);
 
          //Asagidaki dort satir topun yanindaki cephaneligi ciziyor
 
          if(numshots > 3) 
            g.drawImage(shot,0,ymax-((2*shot.getHeight(this))+2),this);
          if(numshots > 2) 
            g.drawImage(shot,2+shot.getWidth(this),ymax-((2*shot.getHeight(this))+2),this);
          if(numshots > 1) 
            g.drawImage(shot,0,ymax-shot.getHeight(this),this);
          if(numshots  >0) 
            g.drawImage(shot,2+shot.getWidth(this),ymax-shot.getHeight(this),this);
 
          if(numshots < 4){
            x = 65;
            y = ymax-55;
            xi = (int)x;
            yi = (int)y;
            dx = velocity * Math.cos(theta);
            dy = velocity * Math.sin(theta);
            g.setColor(Color.gray);

          while((xi > 0) && (xi < xmax) && (yi < ymax) && cannon_applet.work){
               if(xo != 0){
                 g.clearRect(xo - 4, yo - 4, 8, 7);
                 g.setColor(back_ground);
                 g.drawLine(xo-1,yo-1,xo-1+dxi_o,yo-1);  //velocity vectors are erased here
                 g.drawLine(xo-1,yo-1,xo-1,yo-1-dyi_o);
                 g.setColor(Color.black);
                 g.drawLine(65,ymax-55,dim.width-1,ymax-55);  // draw coordinate axises.
                 g.drawLine(65,ymax-55,65,1);
                 g.setColor(Color.gray);
                 if((xi < 70) || ((dyi < 0) && (xi < 130)) ) 
                   { g.drawImage(can, 20, ymax - can.getHeight(this), this);
                     try {Thread.sleep(7);} catch (InterruptedException e){} }
                 if((x>xmax-(targ.getWidth(this)+100)) && (y>ymax-targ.getHeight(this)-60))
                    { g.drawImage(targ,xmax-(targ.getWidth(this)+10),ymax-targ.getHeight(this)-10,this);
                      try {Thread.sleep(7);} catch (InterruptedException e){} }
               }

               
     // draw the moving shot...        
               g.drawImage(shot, xi - 4, yi - 4, this);
    //           g.setColor(Color.black);
    //           g.drawOval(xi - 4, yi - 4, 8,8);
    //           g.setColor(Color.red);
    //           g.fillOval(xi - 4, yi - 4, 8,8);
    //           try {Thread.sleep(8);} catch (InterruptedException e){}
               g.setColor(Color.green);
               dxi=(int)(dx*15);
               dyi=(int)(dy*15);
               dxo = Math.abs(dxi);
               dyo = Math.abs(dyi);
               g.drawLine(xi-1,yi-1,xi-1+dxi,yi-1);  //velocity vectors are drawn here
               g.drawLine(xi-1,yi-1,xi-1,yi-1-dyi);
               try {Thread.sleep(12);} catch (InterruptedException e){}
               g.setColor(Color.gray);

               xo = xi; yo = yi;
               dxi_o = dxi; dyi_o = dyi;
               if((track_number % 6) == 0)
                 { track_p = track_number / 6;
                   tracks_x[track_p]=xi;
                   tracks_y[track_p]=yi;
                   tracks_dx[track_p]=dxi;
                   tracks_dy[track_p]=dyi;
                   if(track_p > 0)
                     { g.drawRect(tracks_x[track_p-1], tracks_y[track_p-1],2,2);
                       if(dxi < 0)
                          for(counter=0;counter<track_p;++counter)
                             g.drawRect(tracks_x[counter],tracks_y[counter], 2, 2);
                     }        
     // drawing the components of the parabolic trajectory...
                   for(counter=0;counter<track_p;counter++)
                     { if(tracks_dx[counter]<0) 
                         { g.setColor(Color.red);
                           g.drawRect(tracks_x[counter],ymax-58,2,2); }
                       else 
                         { g.setColor(Color.blue);
                           g.drawRect(tracks_x[counter],ymax-55,2,2); }
                       if(tracks_dy[counter]<0) 
                         { g.setColor(Color.red);
                           g.drawRect(62,tracks_y[counter],2,2); }
                       else
                         { g.setColor(Color.blue);
                           g.drawRect(65,tracks_y[counter],2,2); }
                     }
                 }  

               g.setColor(Color.gray);
               ++track_number;
               x += dx;
               dx -= windage/2;
               y -= dy;
               dy -= gravity/2;
               xi = (int)x;
               yi = (int)y;

                //##### Check for hit
               if((xi>(xmax-43)) && (xi<(xmax-27)) && (yi>(ymax-40)) && (yi<(ymax-25))){
                   // Bang over full target
                  g.drawImage(bang1, xmax - (targ.getWidth(this)+10),ymax - targ.getHeight(this)-10, this);
                  try {Thread.sleep(175);} catch (InterruptedException e){}
                   // targ2
                g.clearRect(xmax-(targ.getWidth(this)+15), ymax-(targ.getHeight(this)+40),
                            (targ.getWidth(this)+15), (targ.getHeight(this)+40));
                g.drawImage(targ2, xmax - (targ.getWidth(this)+10),ymax - targ2.getHeight(this), this);
                x=-1;
                break;
              }   // if ((xi> ... statement
           }      // while statement
           xo = yo = 0;
           cannon_applet.work=false;
          }
    }
} 

