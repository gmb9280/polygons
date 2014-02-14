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
