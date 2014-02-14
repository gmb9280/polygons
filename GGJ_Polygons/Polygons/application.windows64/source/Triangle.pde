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
  
  void Wander()
  {
    destination = new PVector(random(0,1920), random(0,1080));
    polyState = PolygonState.MOVING;
  }
  
}
