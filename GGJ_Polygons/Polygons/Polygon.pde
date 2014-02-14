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
  void LevelUp()
  {
    level++;
    println("Unit leveled up generically! But how?!"); // lol
  }
  
  // Add experience to what we have. If it is a certain amount (dependant on level),
  // level up. This should not have to be overridden.
  void GainExperience(int additionalExp)
  {
    exp += additionalExp;
    
  }
  
  void Display()
  {
    noTint();

    shape(sprite, location.x - radius*.5, location.y -radius*.5, radius, radius);

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
  
  void Move()
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
        target.radius-=.1; 
        pplayer.numCircles+=.03;
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
  void MoveTo(PVector obj)
  {
    destination = obj;
    int xDist, yDist;
    PVector vecToObj;
    //find the direction we need to go:AKA a vector
    if(location.x < obj.x)
    {
      xDist = int(obj.x-location.x);
    }
    else
    {
      xDist = int(location.x-obj.x);
      xDist = -xDist;
    }
    if(obj.y < location.y)
    {
      yDist = int(location.y - obj.y);
      yDist = -yDist;
    }
    else
    {
      yDist = int(obj.y - location.y);
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
