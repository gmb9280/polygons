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
  
  void LoadAssets()
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
  
  void Display()
  {
    // Draw stuff
  }
  
  void Update()
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
      rect(width/2, height/2, width*.45, height*.45,30);
      rectMode(CORNER);
    }
    UpdateCursor();
    
  }
  
  void UpdateCursor()
  {
    imageMode(CENTER);
    shape( cursor, mouseX, mouseY, 50,50);
    imageMode(CORNER);

  }
  
  void DoKeys(char k)
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
  
  void DoMouseClicked()
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
        if( CalcDist( mouseX, mouseY, int(p.location.x), int(p.location.y)) < (p.radius/2))
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
  
  void SelectFromArea(PVector centroid, int w, int h)
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
  void CheckHover()
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
      if(  CalcDist(mouseX, mouseY, int(f.origin.x), int(f.origin.y)) < diag  )
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
          if( CalcDist( mouseX, mouseY, int(c.position.x), int(c.position.y)) < (c.radius/2))
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
      
      if(CalcDist(mouseX, mouseY, int(p.location.x), int(p.location.y)) < (p.radius))
      {
        p.hovered = true;
      }
      else
      {
        p.hovered = false;
      }
      line(mouseX, mouseY, int(p.location.x), int(p.location.y));
    }
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
  
  void DrawObjectiveMarker()
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
