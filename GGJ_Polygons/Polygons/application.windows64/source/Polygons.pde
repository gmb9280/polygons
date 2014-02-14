// Main class 

Game g;
PFont fillerFont;

boolean debug = false;
boolean dragging = false;
PVector dragBegin;
// Always run in full screen
boolean sketchFullScreen() 
{
  return true;
}


void setup()
{
  fillerFont = createFont("Arial", 30);
  size(1920,1080);
  background(155,222,215);
  dragBegin = new PVector(0,0);
  noCursor();
  // Other one-time setup things here
  // Loading please wait
   textFont(fillerFont);
   textAlign(CENTER, CENTER);
   text("Loading, please wait", width/2, height/2);
   
  g = new Game();
}

void draw()
{
  g.Update();
  g.Display();
}

void keyReleased()
{
  try
  {
    g.DoKeys(key);
  }
  finally{}
  
  if(key == 'd')
  {
    if(debug == true) debug = false;
    else debug = true;
    println("changed debug: " + debug);
  }
}

void mouseClicked()
{
  try
  {
    g.DoMouseClicked();
  }
  finally{}
  
}

void mouseReleased()
{
  if(dragging == true)
  {
    try{
      // Make everything in the selection area selected      
       int wi = int(abs(mouseX-dragBegin.x));
        int he = int(abs(mouseY-dragBegin.y));
        int centroidX = 0; int centroidY = 0;
        if(mouseX > dragBegin.x)
        {
          centroidX = mouseX - (wi/2);
        }
        else
        {
          centroidX = mouseX + (wi/2);
        }
        if(mouseY > dragBegin.y)
        {
          centroidY = mouseY - (he/2);
        }
        else
        {
          centroidY = mouseY + (he/2);
        }
         PVector centroid = new PVector(
           centroidX, centroidY
          );
      if(debug)
      {
        fill(0);
        ellipse(centroid.x, centroid.y, 20,20);
        line(centroid.x, centroid.y, mouseX, mouseY);
      }
      
      g.SelectFromArea(centroid, wi, he);
    }finally{}
  }
  dragging = false;
}
void mouseDragged()
{
  try
  {
    if(dragging == false)
    {
      dragging = true;
      int x = mouseX; 
      int y = mouseY;
      dragBegin = new PVector(x, y);
      
    }
    else
    {
      // if we're in the Game mode....
      /*
        if(g.gs == GameState.GAME)
        {
          // update the square
          stroke(38,222,43);
          strokeWeight(3);
          fill(0,0,0,0);
          beginShape();
          vertex(dragBegin.x, dragBegin.y);
          vertex(mouseX, dragBegin.y);
          vertex(mouseX, mouseY);
          vertex(dragBegin.x, mouseY);
          vertex(dragBegin.x, dragBegin.y);
          endShape();
          
        }*/
      if(debug)
      {
        fill(255,255,0);
        ellipse(dragBegin.x, dragBegin.y, 20,20);
        
        fill(255, 0, 255);
        ellipse(mouseX, mouseY, 20,20);
        
        int wi = int(abs(mouseX-dragBegin.x));
        int he = int(abs(mouseY-dragBegin.y));
        int centroidX = 0; int centroidY = 0;
        if(mouseX > dragBegin.x)
        {
          centroidX = mouseX - (wi/2);
        }
        else
        {
          centroidX = mouseX + (wi/2);
        }
        if(mouseY > dragBegin.y)
        {
          centroidY = mouseY - (he/2);
        }
        else
        {
          centroidY = mouseY + (he/2);
        }
         PVector centroid = new PVector(
           centroidX, centroidY
          );
          fill(255,0,0);
          ellipse(centroid.x, centroid.y, 20,20);
          
          fill(0);
          textAlign(CENTER, CENTER);
          PFont flavor = createFont("Arial",20);
          textFont(flavor);
          text("["+centroid.x +", " + centroid.y +"]\n" +
          wi+"by " + he, width/2, 180);
          
      }
      
    }
  }
  finally{}
  
}
