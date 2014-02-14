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
  
  void Wander()
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
