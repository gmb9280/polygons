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
    scale = int( random(20,200) );
    
    // Create circles with some randomness
    for(int i=0; i<MAX_CIRCLES; i++)
    {
      trees.add(
      new Circle( int(random(origin.x-scale, origin.x+scale)), 
                            int(random(origin.y-scale, origin.y+scale)) 
                            ));
    }
    
    println("forest created at " + origin.x + ", " + origin.y + ".");
  }
  
  
  void Display()
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
