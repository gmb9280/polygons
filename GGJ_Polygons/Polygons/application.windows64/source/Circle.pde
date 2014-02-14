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
    r = int(random(37,100));
    g = int(random(85,193));
    b = int(random(24,139));
    
    radius = int(random(5,50));
    position = new PVector(width,height);
  }
  
  Circle(int x, int y)
  {
     // Randomized colors between accepted values 
     r = int(random(37,100));
     g = int(random(85,193));
     b = int(random(24,139));
    
    radius = int(random(5,50));
    position = new PVector(x,y);
  }
  
  void Display()
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
