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
  
  void DrawMap()
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
  
  void populateForests()
  {
    for(int i = 0; i<NUM_FORESTS; i++)
    { 
      forests.add(new Forest(int(random(0,1920)), int(random(0,maxSize))));
    }
  }
  
  // 1: up, 2:Right, 3: Down, 4:up
  void Pan(int direction)
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
  
  void AddTriangle()
  {
    if(mp.numCircles >= 50)
    {
      playerUnits.add(new Triangle(width/2, height/2, mp, this));
      mp.numCircles-=50;
    }
  }
  
  // Get the next closest circle
  Circle GetNextCircle(PVector start)
  {
    float smallestDist = 2000; // set arbitrarily high
    Circle nearestCircle = new Circle();
    for(int i=0; i<forests.size(); i++)
    {
      Forest f = forests.get(i);
      // Compute diag
      float diag = sqrt(2*(f.scale * f.scale));
      
      // if the start is within the diag...
      if(  CalcDist(int(start.x), int(start.y), int(f.origin.x), int(f.origin.y)) < diag  )
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
          if( CalcDist( mouseX, mouseY, int(c.position.x), int(c.position.y)) < smallestDist)
          {
            // Get the dist and now it's the smallest
            smallestDist = CalcDist( mouseX, mouseY, int(c.position.x), int(c.position.y));
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
  
  int CalcDist(int x1, int y1, int x2, int y2)
  {
    int result = int (sqrt(
      ( (x2 - x1) * (x2-x1) )
                +
      ( (y2 - y1) * (y2-y1) )       
    ) );
    return result;
  }
}
