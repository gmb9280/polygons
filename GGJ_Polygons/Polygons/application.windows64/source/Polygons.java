import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Polygons extends PApplet {

// Main class 

Game g;
PFont fillerFont;

boolean debug = false;
boolean dragging = false;
PVector dragBegin;
// Always run in full screen
public boolean sketchFullScreen() 
{
  return true;
}


public void setup()
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

public void draw()
{
  g.Update();
  g.Display();
}

public void keyReleased()
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

public void mouseClicked()
{
  try
  {
    g.DoMouseClicked();
  }
  finally{}
  
}

public void mouseReleased()
{
  if(dragging == true)
  {
    try{
      // Make everything in the selection area selected      
       int wi = PApplet.parseInt(abs(mouseX-dragBegin.x));
        int he = PApplet.parseInt(abs(mouseY-dragBegin.y));
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
public void mouseDragged()
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
        
        int wi = PApplet.parseInt(abs(mouseX-dragBegin.x));
        int he = PApplet.parseInt(abs(mouseY-dragBegin.y));
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
// Circles do not extend Polygon
// because they are much too different. 
// Circles have a durability based on size.
// Each durability can be mined for one resource circle. 

class Circle
{
  float radius;
  int r, g, b; 
  PVector position;
  boolean hovered = false;
  
  Circle()
  {
    // Randomized colors between accepted values 
    r = PApplet.parseInt(random(37,100));
    g = PApplet.parseInt(random(85,193));
    b = PApplet.parseInt(random(24,139));
    
    radius = PApplet.parseInt(random(5,50));
    position = new PVector(width,height);
  }
  
  Circle(int x, int y)
  {
     // Randomized colors between accepted values 
     r = PApplet.parseInt(random(37,100));
     g = PApplet.parseInt(random(85,193));
     b = PApplet.parseInt(random(24,139));
    
    radius = PApplet.parseInt(random(5,50));
    position = new PVector(x,y);
  }
  
  public void Display()
  {
    noStroke();
    fill(r,g,b);
    if(hovered){ fill(r+50, g+50, b+50);}
    
    ellipse(position.x, position.y, radius, radius);
    
    if(hovered)
    {
      fill(0,0,0,10);
      rectMode(RADIUS);
      rect(position.x, position.y, radius/2, radius/2);
      rectMode(CORNER);
    }
  }
  
}
// A forest is an array list of circles. 
// Due to its structure, it can have a nice
// visual effect (colors, scattering, and silhouette).

class Forest
{
  boolean hovered = false;
  PVector origin;
  ArrayList<Circle> trees;
  
  int MAX_CIRCLES = 20;
  int scale;
  int growthTick = 500;
  // Constructor
  Forest(int x, int y)
  {
    origin = new PVector(x,y);
    trees = new ArrayList<Circle>();
    scale = PApplet.parseInt( random(20,200) );
    
    // Create circles with some randomness
    for(int i=0; i<MAX_CIRCLES; i++)
    {
      trees.add(
      new Circle( PApplet.parseInt(random(origin.x-scale, origin.x+scale)), 
                            PApplet.parseInt(random(origin.y-scale, origin.y+scale)) 
                            ));
    }
    
    println("forest created at " + origin.x + ", " + origin.y + ".");
  }
  
  
  public void Display()
  {
    for(int i=0; i<trees.size(); i++)
    {
      Circle c = trees.get(i);
      if(c.radius == 0)
      {
        trees.remove(i);
        if(debug){
          println("Tree destroyed.");
        }
      }
      else
      {
        c.Display();
      } 
    }
    /*if(growthTick == 0)
    {
      trees.add(
        new Circle( int(random(origin.x-scale, origin.x+scale)), 
                              int(random(origin.y-scale, origin.y+scale)) 
                              ));
                              if(debug){ println("Tree added!"); }
                              growthTick = int(random(500,700));
    }
    else{ growthTick--; }*/
    if(debug)
    {
      fill(255,255,255,20);
      if(hovered){ fill(255,0,0,20);}
      rectMode(CENTER);
      rect(origin.x, origin.y, scale*2,scale*2);
      
      strokeWeight(3);
      stroke(0,255,0);
      line(origin.x, origin.y, mouseX, mouseY);
    }
  }
}
class Game
{
  // Attributes like Player and stuff
  GameState gs;
  Map map;
  Player player; // first definition of the player
  // Art attributes
  PImage paper;
  PShape cursor; 
  PShape objective;
  PFont titleFont;
  PFont subFont;
  
  PShape triImage;
  PShape squareImage;
  
  HUD hud;
  int timerMax = 50;
  int mouseTimer = timerMax;
  PVector objLoc;
  // Default constructor
  Game()
  {
    println("Started a new game.");
    gs = GameState.TITLE;
    objLoc = new PVector();
    
    player = new Player();
    map = new Map(player);
    hud = new HUD(player);
    LoadAssets();
    
  }
  
  public void LoadAssets()
  {
    paper = loadImage("art/paper.png");
    titleFont = createFont("Hopeless Place", 120);
    cursor = loadShape("art/cursor.svg");
    subFont = createFont("Arial", 30);
    triImage = loadShape("art/tri.svg");
    squareImage = loadShape("art/square.svg");
    objective = loadShape("art/objective.svg");
    println("finished loading");
  }
  
  public void Display()
  {
    // Draw stuff
  }
  
  public void Update()
  {
    // Update stuff
    if(gs == GameState.TITLE)
    {
      //background(98,29,42);
      
      image(paper, 0,0);
      tint(98, 29, 42);
      
      // Draw the title of the game
      textFont(titleFont);
      textAlign(CENTER, CENTER);
      text("Polygons", width/2, height/2);
      
      textFont(subFont);
      text("[Press Enter]", width/2, height/2 + 150);
    }
    
    if(gs == GameState.INSTRUCTIONS)
    {
      image(paper, 0,0);
      tint(242,230,211);
      textFont(titleFont);
      fill(0);
      textAlign(CENTER, CENTER);
      text("Instructions", width/2, 150);
      textFont(subFont);
      text("You are Deca the Decahedron,\nsupreme overlord of righteous Polygons.\n Click or click and drag to select your polygons.\n Order them to go somewhere or collect circles with a right click.\nBuilding more units costs Circles.\n Press P to pause and ESC to quit.\n\n Game by Gabrielle Bennett", 
      width/2, height/2);
     
      textFont(subFont);
       text("[Press Enter to Continue]", width/2, height-150);
      
    }
    if(gs == GameState.GAME)
    {
      CheckHover();
      image(paper, 0,0);
      tint(179,179,93);
      map.DrawMap();
      
      if(dragging)
      {
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
      }
      DrawObjectiveMarker();
      hud.Display();
    }
    if(gs == GameState.PAUSE)
    {
      image(paper, 0,0);
      tint(179,179,93);
      rectMode(RADIUS);
      fill(100);
      stroke(0);
      rect(width/2, height/2, width*.45f, height*.45f,30);
      rectMode(CORNER);
    }
    UpdateCursor();
    
  }
  
  public void UpdateCursor()
  {
    imageMode(CENTER);
    shape( cursor, mouseX, mouseY, 50,50);
    imageMode(CORNER);

  }
  
  public void DoKeys(char k)
  {
        if(gs == GameState.TITLE)
        {
          if(k == ENTER)
          {
            // Press Enter to continue and the game state will change to Instructions
            gs = GameState.INSTRUCTIONS;
          }
        }
        
        else if(gs == GameState.INSTRUCTIONS)
        {
          if(k == ENTER)
          {
            gs = GameState.GAME;
          }
        }
        else if(gs == GameState.GAME)
        {
          if(k == 'P' || k == 'p')
          {
            gs = GameState.PAUSE;
          }
        }
        else if(gs == GameState.PAUSE)
        {
          if(k == 'P' || k == 'p')
          {
            gs = GameState.GAME;
            
          }
        }
  }
  
  public void DoMouseClicked()
  {
    // What's at my mouse location?
    if(debug)
    {
      fill(200,30,30);
      ellipse(mouseX, mouseY, 150,150);
    }
    // What happens next depends on which mouseButton is clicked
    if(mouseButton == LEFT)
    {
      if(debug){ println("left click"); }
      // First check to see if it's on any of the icons
      if((mouseX >= 20 && mouseX <= 120) && (mouseY >= 210 && mouseY <=310))
      {
        map.AddTriangle();
      }
      
      // If you aren't clicking on a single polygon to select just that one
      // and deselect all others, 
      // you are deselecting all of them
      for(int i =0; i<map.playerUnits.size(); i++)
      {
        Polygon p = map.playerUnits.get(i); 
        if( CalcDist( mouseX, mouseY, PApplet.parseInt(p.location.x), PApplet.parseInt(p.location.y)) < (p.radius/2))
        {
            p.selected = true;
        }
        else
        {
          p.selected = false;
        }
      }
    }
    else if(mouseButton == RIGHT)
    {
      Circle possibleTarget = null;
      // Draw the objective marker
      shape(objective,mouseX, mouseY, 50,50);
      objLoc = new PVector(mouseX, mouseY);
      mouseTimer--;
      // Check to see if any circles are hovered
      for(int j=0; j<map.forests.size(); j++)
      {
        Forest f = map.forests.get(j);
        if(possibleTarget == null)
        {
          for(int k=0; k<f.trees.size(); k++)
          {
            Circle c = f.trees.get(k);
            if(c.hovered == true)
            {
              possibleTarget = c; // set the target
            }
          }
        }
      }
     
      
      // Move all selected units to the objective marker
      for(int i=0; i<map.playerUnits.size(); i++)
      {
        Polygon p = map.playerUnits.get(i);
        if(p.selected == true)
        {
          p.MoveTo(objLoc);
          if(possibleTarget != null)
          {
            p.target = possibleTarget;
            if(debug)
            {
              println("Target set!");
            }
          }
          else
          {
            p.target = null;
          }
          //println("moving to a location");
        }
      }
    }
  }
  
  public void SelectFromArea(PVector centroid, int w, int h)
  {
    // Check all player-controlled polygons.
   println("Checking polygons to make selected.");
   for(int i =0; i< map.playerUnits.size(); i++)
   {
     Polygon p = map.playerUnits.get(i);
     if(p.location.x >= (centroid.x-(w/2)) && p.location.x <= (centroid.x+(w/2)))
     {
       if(p.location.y >= (centroid.y-(h/2)) && p.location.y <= (centroid.y+(h/2)))
       {
         p.selected=true;
       }
       
     }
     else
     {
       p.selected= false;
     }
   }
  }
  
  // Sees which circles are being hovered over
  // depending on which forests are being hovered over
  // LITERAL quad trees : ) 
  public void CheckHover()
  {
    if((mouseX >= 20 && mouseX <= 120) && (mouseY >= 210 && mouseY <=310))
      {
        // hover over icon
        fill(255,255,0,20);
        rect(20,210,100,100,10);
      }
    // Check what the cursor is over.
    // Trees for now
    for(int i=0; i<map.forests.size(); i++)
    {
      Forest f = map.forests.get(i);
      // Compute diag
      float diag = sqrt(2*(f.scale * f.scale));
      
      // if the mouse is within the diag...
      if(  CalcDist(mouseX, mouseY, PApplet.parseInt(f.origin.x), PApplet.parseInt(f.origin.y)) < diag  )
      {
        // Within range of that forest. Check the trees! 
        f.hovered = true;
        
        if(debug)
        {
          strokeWeight(5);
          stroke(255,0,0);
          line(f.origin.x, f.origin.y, mouseX, mouseY);
        }
        for(int j = 0; j<f.trees.size(); j++)
        {
          Circle c = f.trees.get(j);
          if( CalcDist( mouseX, mouseY, PApplet.parseInt(c.position.x), PApplet.parseInt(c.position.y)) < (c.radius/2))
          {
            c.hovered = true;
          }
          else
          {
            c.hovered = false;
          }
        }
        
      }
      else
      {
        f.hovered =false; 
      }
      
     
    }
    
    // Check polygons
    for(int k = 0; k < map.playerUnits.size(); k++)
    {
      Polygon p = map.playerUnits.get(k);
      
      if(CalcDist(mouseX, mouseY, PApplet.parseInt(p.location.x), PApplet.parseInt(p.location.y)) < (p.radius))
      {
        p.hovered = true;
      }
      else
      {
        p.hovered = false;
      }
      line(mouseX, mouseY, PApplet.parseInt(p.location.x), PApplet.parseInt(p.location.y));
    }
  }

  public int CalcDist(int x1, int y1, int x2, int y2)
  {
    int result = PApplet.parseInt (sqrt(
      ( (x2 - x1) * (x2-x1) )
                +
      ( (y2 - y1) * (y2-y1) )       
    ) );
    return result;
  }
  
  public void DrawObjectiveMarker()
  {
     //Check the mouseTimer
      if(mouseTimer <timerMax)
      {
        if(mouseTimer >0)
        {mouseTimer--; 
        shapeMode(CENTER);
          shape(objective, objLoc.x, objLoc.y, 50, 50); 
        shapeMode(CORNER); }
        else{ mouseTimer=timerMax;  }
      }
  }
  
  
}
// This class will be in charge of updating and displaying the heads-up UI

class HUD
{
  PShape deca = loadShape("art/deca.svg");
  PFont flavorFont = createFont("Arial", 10);
  PShape addTri = loadShape("art/addTriangle.svg");
  Player p;
  HUD(Player pl)
  {
    p = pl;
    if(debug) 
    {
      println("HUD created.");
    }
  }
  
  public void Display()
  {
    fill(100);
    stroke(200);
    rectMode(RADIUS);
    rect(250,50,70,25,7);
    rectMode(CORNER);
    // Draw the number of circles for the player
    textAlign(LEFT);
    fill(255);
    text("Circles:"  + PApplet.parseInt(p.numCircles), 210, 50);
    
    
    fill(40);
    stroke(200);
    ellipse(105, 105, 200,200);
    shapeMode(CENTER);
    shape(deca, 105,100, 100,100);
    shapeMode(CORNER);
    textFont(flavorFont);
    textAlign(CENTER);
    fill(255);
    text("Deca\n The Decahedron", 105,170);
    DrawIcons();
  }
  
  public void DrawIcons()
  {
    shape(addTri, 20, 210,100,100);
    
    
  }
}
// This class represents the culmination of 
// lots of different terrain stuff
// and it keeps track of it. k?
class Map
{
  ArrayList<Forest> forests; // Forests contain circle trees
  
  ArrayList obstacles; // Like Lines (implemented later)
  
  ArrayList<Polygon> playerUnits;
  
  ArrayList<Rhombus> wildUnits; 
  
  int maxSize = 1080; 
  
  Player mp;
  
  int NUM_FORESTS = 10;
  // Constructor
  Map(Player play)
  {
    mp = play; // store reference to player
    forests = new ArrayList<Forest>();
    playerUnits = new ArrayList<Polygon>();
    wildUnits = new ArrayList<Rhombus>();
    playerUnits.add(new Triangle(width/2, height/2, play, this ));
    playerUnits.add(new Triangle(width/2+100, height/2, play, this));
    playerUnits.add(new Triangle(width/2, height/2+100, play, this));
    playerUnits.add(new Triangle(width/2-100, height/2, play, this));
    playerUnits.add(new Triangle(width/2, height/2-100, play, this));
    
    wildUnits.add(new Rhombus(500,700,this));
    populateForests();
    println("Map created");
  }
  
  public void DrawMap()
  {
    
    // For each forest, draw the trees
    for(int i=0; i<forests.size(); i++)
    {
      Forest f = forests.get(i);
      f.Display();
      if(f.trees.size() == 0)
      {
        forests.remove(i);
      }
    }
    
    // for each player unit
    for(int j = 0; j<playerUnits.size(); j++)
    {
      Polygon p = playerUnits.get(j);
      p.Display();
    }
    // for each wild unit
    for(int k =0; k<wildUnits.size(); k++)
    {
      Rhombus r = wildUnits.get(k);
      r.Display();
      if(debug)
      {
        stroke(255,0,0);
        line(r.destination.x, r.destination.y, r.location.x, r.location.y);
      }
    }
  }
  
  public void populateForests()
  {
    for(int i = 0; i<NUM_FORESTS; i++)
    { 
      forests.add(new Forest(PApplet.parseInt(random(0,1920)), PApplet.parseInt(random(0,maxSize))));
    }
  }
  
  // 1: up, 2:Right, 3: Down, 4:up
  public void Pan(int direction)
  {
    switch(direction)
    {
      case(1):
        // Move up.
        
      break;
      case(2):
      break;
      case(3):
      break;
      case(4):
      break;
    }
  }
  
  public void AddTriangle()
  {
    if(mp.numCircles >= 50)
    {
      playerUnits.add(new Triangle(width/2, height/2, mp, this));
      mp.numCircles-=50;
    }
  }
  
  // Get the next closest circle
  public Circle GetNextCircle(PVector start)
  {
    float smallestDist = 2000; // set arbitrarily high
    Circle nearestCircle = new Circle();
    for(int i=0; i<forests.size(); i++)
    {
      Forest f = forests.get(i);
      // Compute diag
      float diag = sqrt(2*(f.scale * f.scale));
      
      // if the start is within the diag...
      if(  CalcDist(PApplet.parseInt(start.x), PApplet.parseInt(start.y), PApplet.parseInt(f.origin.x), PApplet.parseInt(f.origin.y)) < diag  )
      {
        // Within range of that forest. Check the trees! 
        f.hovered = true;
        
        if(debug)
        {
          strokeWeight(5);
          stroke(255,0,0);
          line(f.origin.x, f.origin.y, mouseX, mouseY);
        }
        for(int j = 0; j<f.trees.size(); j++)
        {
          Circle c = f.trees.get(j);
          if( CalcDist( mouseX, mouseY, PApplet.parseInt(c.position.x), PApplet.parseInt(c.position.y)) < smallestDist)
          {
            // Get the dist and now it's the smallest
            smallestDist = CalcDist( mouseX, mouseY, PApplet.parseInt(c.position.x), PApplet.parseInt(c.position.y));
            nearestCircle = c; // nearest circle is C
          }
        }
        
      }
      else
      {
        // now smaller; skip
      }
    }
    // Done computing smallest distance
    if(debug)
    {
      stroke(255,0,0);
      line(start.x, start.y, nearestCircle.position.x, nearestCircle.position.y);
    }
    return nearestCircle;
  }
  
  public int CalcDist(int x1, int y1, int x2, int y2)
  {
    int result = PApplet.parseInt (sqrt(
      ( (x2 - x1) * (x2-x1) )
                +
      ( (y2 - y1) * (y2-y1) )       
    ) );
    return result;
  }
}
// Represents the player. 
// Contains a reference to their units 
// or their units and their resources
// also their buildings (when we get there)

class Player
{
  String name;
  float numCircles = 0;
  // Default constructor 
  Player()
  {
    name = "Player1";
  }
  
  Player(String pName)
  {
    name = pName;
  }
}
// This class will encompass all units, friendly or otherwise. 
// It is the base class and a lot of others will inherit from it. 


class Polygon
{
  // Attributes
  int sides;
  float radius;
  PolygonState polyState;
  Allegiance alleg; 
  Player pplayer; // reference to player
  boolean hovered = false;
  boolean selected = false;
  
  // Battle attributes
  int level; 
  int exp;
  
  int currentHealth;
  int maxHealth;
  int attack; // Attacking power
  int defense; // Defending capability
  int speed; // Represents how fast the unit can move compared to others
  
  PVector location;
  PVector direction; // the direction we are moving
  Circle target = null;
  PVector destination;
  // Graphical attributes: blank until constructor comes into play
  PShape sprite;
  PFont flavorFont;
  Map m;
  // Sound attributes: The sound that will play for that poly
  
  // Constructor, default: player-controlled triangle with minimum stats
  
  Polygon(int x, int y, Player pl, Map ma)
  {
    flavorFont = createFont("Arial", 10);
    pplayer = pl;
    m = ma;
    location = new PVector(x,y);
    destination = new PVector();
    direction = new PVector();
    // Set current state to waiting upon creation.
    polyState = PolygonState.WAITING;
    sides = 3; // Default to triangle
    alleg = Allegiance.PLAYER; // Default to player having control
    level = 1; 
    exp = 0; 
    currentHealth = 10;
    maxHealth = 10;
    attack = 1;
    defense = 1;
    speed = 3;
    
    radius = 70;
    println("Polygon constructor complete with state: " + polyState );
  }
  Polygon(int x, int y, Map ma)
  {
    flavorFont = createFont("Arial", 10);
    m = ma;
    location = new PVector(x,y);
    destination = new PVector();
    direction = new PVector();
    // Set current state to waiting upon creation.
    polyState = PolygonState.WAITING;
    sides = 3; // Default to triangle
    alleg = Allegiance.PLAYER; // Default to player having control
    level = 1; 
    exp = 0; 
    currentHealth = 10;
    maxHealth = 10;
    attack = 1;
    defense = 1;
    speed = 3;
    
    radius = 70;
    println("Polygon constructor complete with state: " + polyState );
  }
  
  // Parameterized constructor for subclasses
  
  // Level up function: level increases and stats do too, depending on what type of poly. 
  // OVERRIDE IN THE CHILDREN CLASSES PLEASE
  public void LevelUp()
  {
    level++;
    println("Unit leveled up generically! But how?!"); // lol
  }
  
  // Add experience to what we have. If it is a certain amount (dependant on level),
  // level up. This should not have to be overridden.
  public void GainExperience(int additionalExp)
  {
    exp += additionalExp;
    
  }
  
  public void Display()
  {
    noTint();

    shape(sprite, location.x - radius*.5f, location.y -radius*.5f, radius, radius);

    if(hovered)
    {
      fill(255,255,0,50);
      ellipse(location.x, location.y, radius, radius);
      
    }
    else if(selected)
    {
      fill(0,255,0,50);
      ellipse(location.x, location.y, radius, radius);
    }
    
    if(debug)
    {
      fill(0);
      ellipse(location.x, location.y, 5,5);
      textFont(flavorFont);
      fill(255,0,0);
      text(currentHealth+"/"+maxHealth, location.x-(radius/2), location.y+(radius/2));
    }
    if(currentHealth < maxHealth)
    {
      textFont(flavorFont);
      fill(255,0,0);
      text(currentHealth+"/"+maxHealth, location.x-(radius/2), location.y+(radius/2));
    }
    Move();
  }
  
  public void Move()
  {
    if(polyState == PolygonState.MOVING)
    {
      if((location.x < (destination.x-5) || location.x > (destination.x+5)) || (location.y < (destination.y -5) && location.y > (destination.y +5)))
      {
        location.x += (direction.x * speed);
        location.y += (direction.y * speed);
        if(debug){ println("Moving to destination: " + destination +", now with location " + location); }
      }
      // Check whether or not we've reached the destination
      else if((location.x > (destination.x-5) || location.x < (destination.x+5)) && (location.y > (destination.y -5) && location.y < (destination.y +5)))
      {
        if(target == null)
        {
          // done if we don't have a circle target
          polyState = PolygonState.WAITING;
          if(debug){ println("now waiting"); }
          return;
        }
        else if(target != null)
        {
          if(debug){ println("at the target. now eliminating."); }
          polyState = PolygonState.GATHERING;
        }
      }
    }
    if(polyState == PolygonState.GATHERING)
    {
      // Whittle down the radius of the target if we still have a target
      if(target.radius >0)
      {
        target.radius-=.1f; 
        pplayer.numCircles+=.03f;
      }
      else
      {
        // find a new target
        if(debug){ println("Target eliminated"); }
        target = m.GetNextCircle(this.location);
        polyState = PolygonState.MOVING; 
        MoveTo(target.position);
      }
    }
  }
  
  
  
  // When ordered to move somewhere, this will be fired
  public void MoveTo(PVector obj)
  {
    destination = obj;
    int xDist, yDist;
    PVector vecToObj;
    //find the direction we need to go:AKA a vector
    if(location.x < obj.x)
    {
      xDist = PApplet.parseInt(obj.x-location.x);
    }
    else
    {
      xDist = PApplet.parseInt(location.x-obj.x);
      xDist = -xDist;
    }
    if(obj.y < location.y)
    {
      yDist = PApplet.parseInt(location.y - obj.y);
      yDist = -yDist;
    }
    else
    {
      yDist = PApplet.parseInt(obj.y - location.y);
    }
    if(debug)
    {
      line(location.x, location.y, location.x+xDist, location.y+yDist);
    }
    
    // Time to normalize the vector
    vecToObj = new PVector(xDist, yDist);
    vecToObj.normalize(); // that was easy
    direction = vecToObj; // set direction
    polyState = PolygonState.MOVING;
    
  }
}
class Rhombus extends Polygon
{
  
  Rhombus(int x, int y, Map maa)
  {
    super(x,y,maa);
    speed = 2;
    maxHealth = 15; currentHealth = 15;
    alleg = Allegiance.TILTED;
    sprite = loadShape("art/rhombus.svg");
    destination = new PVector(random(0,1920), random(0,1080));
    MoveTo(destination);
  }
  
  public void Wander()
  {
    // if we're at our destination, choose a new one
    if( (location.x > (destination.x-5) || location.x < (destination.x+5)) && (location.y > (destination.y -5) && location.y < (destination.y +5)))
    {
      destination = new PVector(random(0,1920), random(0,1080));
      polyState = PolygonState.MOVING;
      MoveTo(destination);
      println("Chose a new destination!");
    }
    else
    {
      Move();
      if(debug){ println("Rhombus on the move!"); }
    
    }
  }
  
  
}
// An adolescent polygon. These polygons cannot fight very well.
// Until they grow up into squares, they mostly only gather circles and build things.

class Triangle extends Polygon
{
  
  // Blank constructor
  Triangle(int x, int y, Player p, Map maa)
  {
    
 	// Just use the parent's default constructor
    super(x,y,p,maa);
    this.sprite = loadShape("art/tri.svg");
  }
  
  public void Wander()
  {
    destination = new PVector(random(0,1920), random(0,1080));
    polyState = PolygonState.MOVING;
  }
  
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "Polygons" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
