/*
    TUIO processing demo - part of the reacTIVision project
    http://reactivision.sourceforge.net/

    Copyright (c) 2005-2009 Martin Kaltenbrunner <mkalten@iua.upf.edu>

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

// we need to import the TUIO library
// and declare a TuioProcessing client variable
import TUIO.*;
TuioProcessing tuioClient;
import java.util.*;

// these are some helper variables which are used
// to create scalable graphical feedback
float cursor_size = 15;
float object_size = 60;
float table_size = 760;
float scale_factor = 1;
PFont font;

mypoint [] fiducials = new mypoint[256];
boolean [] changed = new boolean[256];
mypoint [] pos = new mypoint[4];
boolean busy = false;

double [] savedCam = new double[3];
double [][] savedFiduciels = new double[4][2];

void setup()
{
  //size(screen.width,screen.height);
  size(640,480);
  noStroke();
  fill(0);
  
  loop();
  frameRate(30);
  //noLoop();
  
  hint(ENABLE_NATIVE_FONTS);
  font = createFont("Arial", 18);
  scale_factor = height/table_size;
  
  // we create an instance of the TuioProcessing client
  // since we add "this" class as an argument the TuioProcessing class expects
  // an implementation of the TUIO callback methods (see below)
  tuioClient  = new TuioProcessing(this);
  
  for(int k = 0 ; k < 256 ; k++)
    changed[k] = false;
  
  pos[0]= new mypoint(0,0,0); //60
  pos[1]= new mypoint(6,0,0); //61
  pos[2]= new mypoint(6,6,0); //62
  pos[3]= new mypoint(0,6,0); //63
}

// within the draw method we retrieve a Vector (List) of TuioObject and TuioCursor (polling)
// from the TuioProcessing client and then loop over both lists to draw the graphical feedback.
void draw()
{
  background(255);
  textFont(font,18*scale_factor);
  float obj_size = object_size*scale_factor; 
  float cur_size = cursor_size*scale_factor; 
   
  Vector tuioObjectList = tuioClient.getTuioObjects();
  for (int i=0;i<tuioObjectList.size();i++) {
     TuioObject tobj = (TuioObject)tuioObjectList.elementAt(i);
     stroke(0);
     fill(0);
     pushMatrix();
     translate(tobj.getScreenX(width),tobj.getScreenY(height));
     rotate(tobj.getAngle());
     rect(-obj_size/2,-obj_size/2,obj_size,obj_size);
     popMatrix();
     fill(255);
     text(""+tobj.getSymbolID(), tobj.getScreenX(width), tobj.getScreenY(height));
   }
   
   Vector tuioCursorList = tuioClient.getTuioCursors();
   for (int i=0;i<tuioCursorList.size();i++) {
      TuioCursor tcur = (TuioCursor)tuioCursorList.elementAt(i);
      Vector pointList = tcur.getPath();
      
      if (pointList.size()>0) {
        stroke(0,0,255);
        TuioPoint start_point = (TuioPoint)pointList.firstElement();;
        for (int j=0;j<pointList.size();j++) {
           TuioPoint end_point = (TuioPoint)pointList.elementAt(j);
           line(start_point.getScreenX(width),start_point.getScreenY(height),end_point.getScreenX(width),end_point.getScreenY(height));
           start_point = end_point;
        }
        
        stroke(192,192,192);
        fill(192,192,192);
        ellipse( tcur.getScreenX(width), tcur.getScreenY(height),cur_size,cur_size);
        fill(0);
        text(""+ tcur.getCursorID(),  tcur.getScreenX(width)-5,  tcur.getScreenY(height)+5);
      }
   }
   
}

void keyPressed(){
  switch(key) {
    case(10):case(13):
    getCam();
	break;
  case('1') : 
     for(int l = 0 ; l < 1 ; l++)
       blockPosition(fiducials[9].x,fiducials[9].y);
  break;
  /*  case('d'):case('D'):result |=EAST;break;
    case('s'):case('S'):result |=SOUTH;break;
    case('a'):case('A'):result |=WEST;break;*/
  }
}

// these callback methods are called whenever a TUIO event occurs

// called when an object is added to the scene
void addTuioObject(TuioObject tobj) {
fiducials[tobj.getSymbolID()] = new mypoint(tobj.getX(),tobj.getY(),tobj.getAngle());
  changed[tobj.getSymbolID()] = true;

}

// called when an object is removed from the scene
void removeTuioObject(TuioObject tobj) {

}

// called when an object is moved
void updateTuioObject (TuioObject tobj) {

          
  fiducials[tobj.getSymbolID()] = new mypoint(tobj.getX(),tobj.getY(),tobj.getAngle());
  changed[tobj.getSymbolID()] = true;
  
  
  /*if(tobj.getSymbolID() == 9)
     blockPosition(fiducials[9].x,fiducials[9].y);*/
 // getCam();
  
}

// called when a cursor is added to the scene
void addTuioCursor(TuioCursor tcur) {
 
}

// called when a cursor is moved
void updateTuioCursor (TuioCursor tcur) {
 
}

// called when a cursor is removed from the scene
void removeTuioCursor(TuioCursor tcur) {

}

// called after each message bundle
// representing the end of an image frame
void refresh(TuioTime bundleTime) { 
  redraw();
}

double sqr(double v)
  {
   return v*v; 
  }

void getCam()
{
  mypoint [] myfiducails = new mypoint[4];
  //60 61 62 63   

      myfiducails[0] = fiducials[60];
      myfiducails[1] = fiducials[61];
      myfiducails[2] = fiducials[62]; 
      myfiducails[3] = fiducials[63]; 
      
	  for(int k = 0 ; k < 4 ; k++)
	  {
	  savedFiduciels[k][0] = myfiducails[k].x;
	  savedFiduciels[k][1] = myfiducails[k].y;
	  }
    double [] angles = new double [6];
    

    angles[0] = Math.sqrt(sqr((myfiducails[0].x- myfiducails[1].x)*51)+sqr((myfiducails[0].y- myfiducails[1].y)*39)); 
    angles[1] = Math.sqrt(sqr((myfiducails[1].x- myfiducails[2].x)*51)+sqr((myfiducails[1].y- myfiducails[2].y)*39));
    angles[2] = Math.sqrt(sqr((myfiducails[2].x- myfiducails[3].x)*51)+sqr((myfiducails[2].y- myfiducails[3].y)*39));
    angles[3] = Math.sqrt(sqr((myfiducails[3].x- myfiducails[0].x)*51)+sqr((myfiducails[3].y- myfiducails[0].y)*39));
    angles[4] = Math.sqrt(sqr((myfiducails[0].x- myfiducails[2].x)*51)+sqr((myfiducails[0].y- myfiducails[2].y)*39));
    angles[5] = Math.sqrt(sqr((myfiducails[1].x- myfiducails[3].x)*51)+sqr((myfiducails[1].y- myfiducails[3].y)*39));
    
    System.out.println("busy");
		for(int k = 0 ; k < 1 ; k++)
		{
		mypoint res= CameraCalibration.getCameraPosition(pos[0],pos[1],pos[2],pos[3],angles);
		System.out.println(res.x+"="+res.y+"="+res.z);
		savedCam[0] = res.x;
		savedCam[1] = res.y;
		savedCam[2] = res.z;
		}


    }
	
	void blockPosition(double x, double y)
	{
	double [] angles = new double [4];
	for(int k = 0 ; k < 4 ; k++)
       angles[k] = Math.sqrt(sqr((savedFiduciels[k][0]- x)*51)+sqr((savedFiduciels[k][1]- y)*39)); 

	
	mypoint [] vecs = new mypoint[4];
	for(int k = 0 ; k < 4 ; k++)
	{
	vecs[k] = new mypoint(-(savedCam[0]-pos[k].x),-(savedCam[1]-pos[k].y),-(savedCam[2]-pos[k].z));
	}

	
	
	mypoint res = CameraCalibration.getBlockVector(vecs,angles);
	System.out.println(res.x+" , "+res.y+" , "+res.z);
	}

