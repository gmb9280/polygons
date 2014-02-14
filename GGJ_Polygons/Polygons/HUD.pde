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
  
  void Display()
  {
    fill(100);
    stroke(200);
    rectMode(RADIUS);
    rect(250,50,70,25,7);
    rectMode(CORNER);
    // Draw the number of circles for the player
    textAlign(LEFT);
    fill(255);
    text("Circles:"  + int(p.numCircles), 210, 50);
    
    
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
  
  void DrawIcons()
  {
    shape(addTri, 20, 210,100,100);
    
    
  }
}
