//Kevin Zhu
//From a data file, you pick 4 pokemon to battle against all other pokemon. All pokemon stats are used in battling.
import java.util.*;
import java.io.*;

class Pokemon{
  private String name,type,resistance,weakness,attackname,special;
  private int hp,numattacks,attackcost,attackdamage,energy=50;
  private String line;
  private boolean disabled=false; //for disable
  private ArrayList<Attack>attacks=new ArrayList<Attack>();
  
  public Pokemon(String line){
    this.line=line;
    String[]stats=line.split(","); //each piece of info from data file goes into string[]stats

    //assigning stats
    name=stats[0];
    hp=Integer.parseInt(stats[1]);
    type=stats[2];
    resistance=stats[3];
    weakness=stats[4];
    numattacks=Integer.parseInt(stats[5]);
    String attackname="";
    String special="";
    energy=50;
  
    for(int i = 0; i<numattacks; i++){ //assign stats for each attack
      attackname=stats[6+i*4];  
      attackcost=Integer.parseInt(stats[7+i*4]);
      attackdamage=Integer.parseInt(stats[8+i*4]);
      special=stats[9+i*4];
      attacks.add(new Attack(attackname, attackcost, attackdamage, special)); //goes to attack class
    }
  }
  @Override //prevents printing in arrayform
  public String toString(){     //all get/set methods to be used in main
    return line;    
  }
  public String getNAME(){
    return name;
  }
  public int getHP(){
    return hp;
  }
  public String getTYPE(){
    return type;
  } 
  public String getRESIST(){
    return resistance;
  }
  public String getWEAK(){
    return weakness;
  }
  public int getNumAttacks(){
    return numattacks;
  }
  public ArrayList<Attack> getATKInfo(){ 
    return attacks;
  }
  public int getNRG(){
    return energy;
  }
  public void setNRG(int nrg){
    energy=nrg; 
  }
  public void setHP(int health){
    hp=health;
  }  
  public boolean disabledornot(){
    return disabled;
  }
  public void setDisable(boolean condition){
    disabled=condition;
  }
} 
public class Main {
  private static ArrayList<Pokemon>pokemonlist=new ArrayList<Pokemon>(); //to your own from, then the rest are given to enemy
  public static ArrayList<Pokemon>team=new ArrayList<Pokemon>();
  public static ArrayList<Pokemon>temphold=new ArrayList<Pokemon>();//temp hold pokemon for retreat when you choose another pokemon to replace active one
  
  public static void main(String[] args)throws IOException { //order of events
    load(); 
    pokemonassign(); //choosing
    battle();
 
  }

  public static void load()throws IOException{ //loading pokemon and stats
		Scanner inFile = new Scanner(new BufferedReader(new FileReader("pokemon.txt")));
    int num=Integer.parseInt(inFile.nextLine());
    for(int i=0; i<num; i++){
      String line=inFile.nextLine();
      Pokemon p=new Pokemon(line);
      pokemonlist.add(p);
    }
    inFile.close();
  }

  public static void pokemonassign(){ //assigning pokemon to your team, and the remaining go to the enemy
    Scanner kb=new Scanner(System.in);
    while(team.size()<4){
      for(int i=0; i< pokemonlist.size(); i++){
        System.out.printf("%3d. %s",i+1, pokemonlist.get(i).getNAME()); //prints all available pokemon to choose from
      }
      System.out.println("");
      System.out.println("Enter Pokemon by number:");
      int num = Integer.parseInt(kb.nextLine());
      team.add(pokemonlist.get(num-1));
      pokemonlist.remove(num-1); //remaining in arraylist is for enemies
    }
    System.out.println("Great choice! You've picked "+team.get(0).getNAME()+", "+team.get(1).getNAME()+", "+team.get(2).getNAME()+", "+team.get(3).getNAME());
  }
 
  public static int randint(int low, int high){
		return (int)(Math.random()*(high-low+1)+low);
	}

  public static void pokeselectionafterfaint(Pokemon activePoke,Pokemon enemyPoke){  //after one of your pokemon faints, this method is called to choose your next pokemon to deploy
    System.out.println("Choose Pokemon to replace your fainted Pokemon!");
    Scanner choosepoke=new Scanner(System.in);
    for(int i=0;i<team.size();i++){
      if(team.get(i)!=activePoke){
        System.out.println(i+". "+team.get(i).getNAME()); 
      }
    }
    int chosenpokemon=choosepoke.nextInt();
    activePoke=team.get(chosenpokemon-1);
    System.out.println(activePoke.getNAME()+"I CHOOSE YOU!");
    performAction(activePoke,enemyPoke); //method called, where user gets to pick attack/retreat/pass
  }
 
  public static void battle(){ //actual initiation of battle.
    Pokemon enemyPoke = pokemonlist.get(randint(0,pokemonlist.size())); //picks rand pokemon from pokemonlist
    System.out.println("Enemy trainer chooses "+enemyPoke.getNAME()+"!");
    Scanner kb=new Scanner(System.in);
    int chosenpokemon;
    System.out.println("Choose Pokemon to deploy!");
    for(int i=1;i<team.size()+1;i++){
      System.out.println(i+". "+team.get(i-1).getNAME()); //choose pokemon to deploy
    }
    chosenpokemon=kb.nextInt(); //index for list of chosen pokemon
    Pokemon activePoke=team.get(chosenpokemon-1); //pokemon you chose to deploy
    System.out.println(activePoke.getNAME()+", I CHOOSE YOU!");
    
    int whogoesfirst=randint(0,1); //for choosing who goes first
    if(whogoesfirst==1){
      performAction(activePoke,enemyPoke); //user
    }
    else{
      enemyPerformAttack(activePoke,enemyPoke);//computer
    }
  }
  public static void performAction(Pokemon activePoke,Pokemon enemyPoke){
    Scanner whichaction=new Scanner(System.in);
    System.out.println("Choose Action:");
    System.out.println("1.Attack 2.Retreat 3.Pass");
    int response=whichaction.nextInt();
    if(response==1){ //if attack
      performAttack(activePoke,enemyPoke); //actual attack
    }

    if(response==2){ //if retreat
      System.out.println("Choose Pokemon to replace your current active Pokemon!");
      Scanner choosepoke=new Scanner(System.in);
      for(int i=1;i<team.size();i++){
        if(team.get(i-1)!=activePoke){
          System.out.println(i+". "+team.get(i).getNAME()); //prints remaining pokemon on team
        }
      }
      int chosenpokemon=choosepoke.nextInt();
      temphold.add(activePoke); //this ALIST to ensure pokemon isnt completely deleted, is added back to team after new pokemon deployed
      team.remove(activePoke);
      activePoke=team.get(chosenpokemon-1);
      System.out.println(activePoke.getNAME()+", I CHOOSE YOU!"); //active pokemon is replaced with newly chosen from team
      team.add(temphold.get(0));
      temphold.remove(0);
    }
    if(response==3){ //if pass
      for(int k=0;k<team.size();k++){     //all team pokemon replenish 10 energy
        if(50-team.get(k).getNRG()<10){ 
          team.get(k).setNRG(50);
        }
        else{
          team.get(k).setNRG(team.get(k).getNRG()+10);
        }  
      }
      enemyPerformAttack(activePoke,enemyPoke);//after user turn, its enemy's turn
    }
  }
  public static void userstunspecial(Pokemon activePoke,Pokemon enemyPoke,int atknum){
    int specialchance=randint(0,1);//50% chance of success
    if(specialchance==1){
      if(activePoke.getTYPE().equals(enemyPoke.getWEAK())){
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()*2);//double the damage if user's pokemon type is the enemy's weakness
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()*2+" to "+enemyPoke.getNAME()+"!");
      }
      else if(activePoke.getTYPE().equals(enemyPoke.getRESIST())){
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()/2);//half damage if users type is the enemy's resistance
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()/2+" to "+enemyPoke.getNAME()+"!");
      }
      else{
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()); //enemy takes regular damage
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()+" to "+enemyPoke.getNAME()+"!");      
      }
      System.out.println("The enemy pokemon has been stunned!");
      for(int k=0;k<team.size();k++){     //team replenish 10 energy
        if(50-activePoke.getNRG()<10){
          activePoke.setNRG(50);
        }
        else{
          activePoke.setNRG(team.get(k).getNRG()+10);
        }
        if(enemyPoke.getHP()>0){ //if enemy is still alive, their turn is skipped
          performAction(activePoke,enemyPoke);
        }
        else if(enemyPoke.getHP()<=0){ //if userpoke died
          if(pokemonlist.size()==0){ //if enemies are all killed
            System.out.println("You are the trainer supreme!");
          }
          else{
            System.out.println(enemyPoke.getNAME()+" has fainted!");
            pokemonlist.remove(enemyPoke);
            battle(); //start next round
          }
        }       
      }
    }
    else{
      if(activePoke.getTYPE().equals(enemyPoke.getWEAK())){
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()*2);//double the damage if user's pokemon type is the enemy's weakness
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()*2+" to "+enemyPoke.getNAME()+"!");
      }
      else if(activePoke.getTYPE().equals(enemyPoke.getRESIST())){
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()/2);//half damage if users type is the enemy's resistance
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()/2+" to "+enemyPoke.getNAME()+"!");
      }
      else{
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()); //enemy takes regular damage
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()+" to "+enemyPoke.getNAME()+"!"); 
      }
      System.out.println("The stun special did not come into effect.");
      for(int k=0;k<team.size();k++){     //all pokemon replenish 10 energy
        if(50-team.get(k).getNRG()<10){
          team.get(k).setNRG(50);
        }
        else{
        team.get(k).setNRG(team.get(k).getNRG()+10);
        }
        if(enemyPoke.getHP()>0){ //if enemypokemon is still alive, it is their turn to attack
        enemyPerformAttack(activePoke,enemyPoke);
        }
        else if(enemyPoke.getHP()<=0){ //if enemypoke died
          if(pokemonlist.size()==0){
            System.out.println("You are the trainer supreme!");
          }
          else{
            System.out.println(enemyPoke.getNAME()+" has fainted!");
            pokemonlist.remove(enemyPoke);
            battle(); //start next round
          }
        }       
      }
    }
  }
  public static void userwildcard(Pokemon activePoke,Pokemon enemyPoke,int atknum){
    int specialchance=randint(0,1);
    if(specialchance==1){
      if(activePoke.getTYPE().equals(enemyPoke.getWEAK())){
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()*2);//double the damage if user's pokemon type is the enemy's weakness
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()*2+" to "+enemyPoke.getNAME()+"!"); 
      }
      else if(activePoke.getTYPE().equals(enemyPoke.getRESIST())){
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()/2);//half damage if users type is the enemy's resistance
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()/2+" to "+enemyPoke.getNAME()+"!"); 
      }
      else{
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()); //enemy takes regular damage
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()+" to "+enemyPoke.getNAME()+"!"); 
      }
      for(int k=0;k<team.size();k++){     //all pokemon replenish 10 energy
        if(50-team.get(k).getNRG()<10){
          team.get(k).setNRG(50);
        }
        else{
        team.get(k).setNRG(team.get(k).getNRG()+10);
        }
        if(enemyPoke.getHP()>0){ //if enemypokemon is still alive, it is their turn to attack
        enemyPerformAttack(activePoke,enemyPoke);
        }
        else if(enemyPoke.getHP()<=0){ //if enemypoke died
          if(pokemonlist.size()==0){
            System.out.println("You are the trainer supreme!");
          }
          else{
            System.out.println(enemyPoke.getNAME()+" has fainted!");
            pokemonlist.remove(enemyPoke);
            battle(); //start next round
          }
        }       
      }
    }
    else{
      System.out.println(activePoke.getATKInfo().get(atknum).getATK()+" failed.");
      for(int k=0;k<team.size();k++){     //all pokemon replenish 10 energy
        if(50-team.get(k).getNRG()<10){
          team.get(k).setNRG(50);
        }
        else{
        team.get(k).setNRG(team.get(k).getNRG()+10);
        }
        if(enemyPoke.getHP()>0){ //if enemypokemon is still alive, it is their turn to attack
        enemyPerformAttack(activePoke,enemyPoke);
        }
        else if(enemyPoke.getHP()<=0){ //if enemypoke died
          if(pokemonlist.size()==0){
            System.out.println("You are the trainer supreme!");
          }
          else{
            System.out.println(enemyPoke.getNAME()+" has fainted!");
            pokemonlist.remove(enemyPoke);
            battle(); //start next round
          }
        }       
      }
    }
  }
  public static void userwildstorm(Pokemon activePoke,Pokemon enemyPoke,int atknum){
    int specialchance=randint(0,1);
    if(specialchance==1){
      if(activePoke.getTYPE().equals(enemyPoke.getWEAK())){
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()*2);//double the damage if user's pokemon type is the enemy's weakness
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()*2+" to "+enemyPoke.getNAME()+"!"); 
      }
      else if(activePoke.getTYPE().equals(enemyPoke.getRESIST())){
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()/2);//half damage if users type is the enemy's resistance
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()/2+" to "+enemyPoke.getNAME()+"!"); 
      }
      else{
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()); //enemy takes regular damage
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()+" to "+enemyPoke.getNAME()+"!");       
      }
      for(int k=0;k<team.size();k++){     //all pokemon replenish 10 energy
        if(50-team.get(k).getNRG()<10){
          team.get(k).setNRG(50);
        }
        else{
        team.get(k).setNRG(team.get(k).getNRG()+10);
        }
        if(enemyPoke.getHP()>0){ //if enemypokemon is still alive, user attacks again
          userwildstorm(activePoke,enemyPoke,atknum); //re-calls method, repeats until specialchance is a 0 (a miss)
        }
        else if(enemyPoke.getHP()<=0){ //if enemypoke died
          if(pokemonlist.size()==0){
            System.out.println("You are the trainer supreme!");
          }
          else{
            System.out.println(enemyPoke.getNAME()+" has fainted!");
            pokemonlist.remove(enemyPoke);
            battle(); //start next round
          }
        }       
      }
    }
    else{
      System.out.println(activePoke.getATKInfo().get(atknum).getATK()+" failed.");
      for(int k=0;k<team.size();k++){     //all pokemon replenish 10 energy
        if(50-team.get(k).getNRG()<10){
          team.get(k).setNRG(50);
        }
        else{
        team.get(k).setNRG(team.get(k).getNRG()+10);
        }
        if(enemyPoke.getHP()>0){ //if enemypokemon is still alive, it is their turn to attack
        enemyPerformAttack(activePoke,enemyPoke);
        }
        else if(enemyPoke.getHP()<=0){ //if enemypoke died
          if(pokemonlist.size()==0){
            System.out.println("You are the trainer supreme!");
          }
          else{
            System.out.println(enemyPoke.getNAME()+" has fainted!");
            pokemonlist.remove(enemyPoke);
            battle(); //start next round
          }
        }       
      }
    }
  }

  public static void userdisablespecial(Pokemon activePoke,Pokemon enemyPoke,int atknum){
    enemyPoke.setDisable(true);
    if(activePoke.getTYPE().equals(enemyPoke.getWEAK())){
      enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()*2-10); //10 less dmg due to disable
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+(activePoke.getATKInfo().get(atknum).getDMG()*2-10)+" to "+enemyPoke.getNAME()+"!"); 
    }
    else if(activePoke.getTYPE().equals(enemyPoke.getRESIST())){
      enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()/2-10); //10 less dmg due to disable
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+(activePoke.getATKInfo().get(atknum).getDMG()/2-10)+" to "+enemyPoke.getNAME()+"!"); 
    }
    else{
      enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()-10); //10 less dmg due to disable
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+(activePoke.getATKInfo().get(atknum).getDMG()-10)+" to "+enemyPoke.getNAME()+"!"); 
    }
    for(int k=0;k<team.size();k++){     //all pokemon replenish 10 energy
      if(50-team.get(k).getNRG()<10){
        team.get(k).setNRG(50);
      }
      else{
      team.get(k).setNRG(team.get(k).getNRG()+10);
      }  
    }

    if(enemyPoke.getHP()>0){ //if enemypokemon is still alive, it is their turn to attack
      enemyPerformAttack(activePoke,enemyPoke);
    }
  
    else if(enemyPoke.getHP()<=0){
      if(pokemonlist.size()==0){
        System.out.println("You are the trainer supreme!");
      }
      else{
        System.out.println(enemyPoke.getNAME()+" has fainted!");
        enemyPoke.setDisable(false);
        pokemonlist.remove(enemyPoke);
        battle(); //start next round
      }
    }
  }
  public static void userrechargespecial(Pokemon activePoke,Pokemon enemyPoke,int atknum){
    activePoke.setHP(activePoke.getHP()+20);
    if(activePoke.getTYPE().equals(enemyPoke.getWEAK())){
      enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()*2); 
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()*2+" to "+enemyPoke.getNAME()+"!");   
    }
    else if(activePoke.getTYPE().equals(enemyPoke.getRESIST())){
      enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()/2); 
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()/2+" to "+enemyPoke.getNAME()+"!"); 
    }
    else{
      enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum).getDMG()); 
        System.out.println(activePoke.getNAME()+" used "+activePoke.getATKInfo().get(atknum).getATK()+" has dealt "+activePoke.getATKInfo().get(atknum).getDMG()+" to "+enemyPoke.getNAME()+"!"); 
    }
    for(int k=0;k<team.size();k++){     //all pokemon replenish 10 energy
      if(50-team.get(k).getNRG()<10){
        team.get(k).setNRG(50);
      }
      else{
      team.get(k).setNRG(team.get(k).getNRG()+10);
      }  
    }

    if(enemyPoke.getHP()>0){ //if enemypokemon is still alive, it is their turn to attack
      enemyPerformAttack(activePoke,enemyPoke);
    }
  
    else if(enemyPoke.getHP()<=0){
      if(pokemonlist.size()==0){
        System.out.println("You are the trainer supreme!");
      }
      else{
        System.out.println(enemyPoke.getNAME()+" has fainted!");
        pokemonlist.remove(enemyPoke);
        battle(); //start next round
      }
    }
  }
  public static void enemystun(Pokemon activePoke,Pokemon enemyPoke,int atknum){
    int specialchance=randint(0,1);
    if(specialchance==1){
      if(enemyPoke.getTYPE().equals(activePoke.getWEAK())){
        activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()*2);//double the damage if enemy's pokemon type is the user's weakness
      }
      else if(enemyPoke.getTYPE().equals(activePoke.getRESIST())){
        activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()/2);//half damage if enemy's type is the user's resistance
      }
      else{
        activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()); //user takes regular damage
      }
      System.out.println(enemyPoke.getNAME()+" has dealt "+enemyPoke.getATKInfo().get(atknum).getDMG()+" to "+activePoke.getNAME()+"!");
      System.out.println("Your pokemon has been stunned!");
      if(50-enemyPoke.getNRG()<10){
        enemyPoke.setNRG(50);
      }
      else{
        enemyPoke.setNRG(enemyPoke.getNRG()+10);
      }
      if(activePoke.getHP()>0){ //if user pokemon is still alive, their turn is skipped
        enemyPerformAttack(activePoke,enemyPoke);
      }
      else if(activePoke.getHP()<=0){ //if user died
        System.out.println(activePoke.getNAME()+" has fainted!");
        team.remove(activePoke);
        if(team.size()==0){
          System.out.println("You've been defeated!");
        }
        else{
          pokeselectionafterfaint(activePoke,enemyPoke);
        }           
      }
    }
    else{
      if(enemyPoke.getTYPE().equals(activePoke.getWEAK())){
        activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()*2);//double the damage if enemy's pokemon type is the user's weakness
      }
      else if(enemyPoke.getTYPE().equals(activePoke.getRESIST())){
        activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()/2);//half damage if enemy's type is the user's resistance
      }
      else{
        activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()); //user takes regular damage
      }
      System.out.println(enemyPoke.getNAME()+" has dealt "+enemyPoke.getATKInfo().get(atknum).getDMG()+" to "+activePoke.getNAME()+"!");
      System.out.println("The stun effect did not take place.");
      if(50-enemyPoke.getNRG()<10){
        enemyPoke.setNRG(50);
      }
      else{
        enemyPoke.setNRG(enemyPoke.getNRG()+10);
      }
      if(activePoke.getHP()>0){ //if user pokemon is still alive, its their turn
        performAction(activePoke,enemyPoke);
      }
      else if(activePoke.getHP()<=0){ //if user died
        System.out.println(activePoke.getNAME()+" has fainted!");
        team.remove(activePoke);
        if(team.size()==0){
          System.out.println("You've been defeated!");
        }
        else{
          pokeselectionafterfaint(activePoke,enemyPoke);
        }               
      }
    }
  }
    public static void enemywildcard(Pokemon activePoke,Pokemon enemyPoke,int atknum){
    int specialchance=randint(0,1);
    if(specialchance==1){
      if(enemyPoke.getTYPE().equals(activePoke.getWEAK())){
        activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()*2);//double the damage if enemy's pokemon type is the user's weakness
      }
      else if(enemyPoke.getTYPE().equals(activePoke.getRESIST())){
        activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()/2);//half damage if enemy's type is the user's resistance
      }
      else{
        activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()); //user takes regular damage
      }
      System.out.println(enemyPoke.getNAME()+" has dealt "+enemyPoke.getATKInfo().get(atknum).getDMG()+" to "+activePoke.getNAME()+"!");
      System.out.println("Wild card worked!");
      if(50-enemyPoke.getNRG()<10){
        enemyPoke.setNRG(50);
      }
      else{
        enemyPoke.setNRG(enemyPoke.getNRG()+10);
      }
      if(activePoke.getHP()>0){ //if user pokemon is still alive, its their turn
        enemyPerformAttack(activePoke,enemyPoke);
      }
      else if(activePoke.getHP()<=0){ //if user died
        System.out.println(activePoke.getNAME()+" has fainted!");
        team.remove(activePoke);
        if(team.size()==0){
          System.out.println("You've been defeated!");
        }
        else{
          pokeselectionafterfaint(activePoke,enemyPoke);
        }          
      }
    }
    else{
      System.out.println("The wild card effect did not take place.");
      if(50-enemyPoke.getNRG()<10){
        enemyPoke.setNRG(50);
      }
      else{
        enemyPoke.setNRG(enemyPoke.getNRG()+10);
      }
      if(activePoke.getHP()>0){ //if user pokemon is still alive, its their turn
        performAction(activePoke,enemyPoke);
      }
      else if(activePoke.getHP()<=0){ //if user died
        System.out.println(activePoke.getNAME()+" has fainted!");
        team.remove(activePoke);
        if(team.size()==0){
          System.out.println("You've been defeated!");
        }
        else{
          pokeselectionafterfaint(activePoke,enemyPoke);
        }             
      }
    }
  }

  public static void enemywildstorm(Pokemon activePoke,Pokemon enemyPoke,int atknum){
    int specialchance=randint(0,1);
    if(specialchance==1){
      if(enemyPoke.getTYPE().equals(activePoke.getWEAK())){
        activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()*2);//double the damage if enemy's pokemon type is the user's weakness
      }
      else if(enemyPoke.getTYPE().equals(activePoke.getRESIST())){
        activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()/2);//half damage if enemy's type is the user's resistance
      }
      else{
        activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()); //user takes regular damage
      }
      System.out.println(enemyPoke.getNAME()+" has dealt "+enemyPoke.getATKInfo().get(atknum).getDMG()+" to "+activePoke.getNAME()+"!");
      System.out.println("Wild storm worked!");
      if(50-enemyPoke.getNRG()<10){
        enemyPoke.setNRG(50);
      }
      else{
        enemyPoke.setNRG(enemyPoke.getNRG()+10);
      }
      if(activePoke.getHP()>0){ //method recalled, it is possible to go on forever
        enemywildstorm(activePoke,enemyPoke,atknum);
      }
      else if(activePoke.getHP()<=0){ //if user died
        System.out.println(activePoke.getNAME()+" has fainted!");
        team.remove(activePoke);
        if(team.size()==0){
          System.out.println("You've been defeated!");
        }
        else{
          pokeselectionafterfaint(activePoke,enemyPoke);
        }             
      }
    }
    else{
      System.out.println("Wild storm failed.");
      if(50-enemyPoke.getNRG()<10){
        enemyPoke.setNRG(50);
      }
      else{
        enemyPoke.setNRG(enemyPoke.getNRG()+10);
      }
      if(activePoke.getHP()>0){ //if user pokemon is still alive, its their turn
        performAction(activePoke,enemyPoke);
      }
      else if(activePoke.getHP()<=0){ //if user died
        System.out.println(activePoke.getNAME()+" has fainted!");
        team.remove(activePoke);
        if(team.size()==0){
          System.out.println("You've been defeated!");
        }
        else{
          pokeselectionafterfaint(activePoke,enemyPoke);
        }          
      }
    }
  }

  public static void enemydisable(Pokemon activePoke,Pokemon enemyPoke,int atknum){
    activePoke.setDisable(true);
    if(enemyPoke.getTYPE().equals(activePoke.getWEAK())){
      activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()*2-10); 
      System.out.println(enemyPoke.getNAME()+" has dealt "+(enemyPoke.getATKInfo().get(atknum-1).getDMG()*2-10)+" to "+activePoke.getNAME()+"!");    
    }
    else if(enemyPoke.getTYPE().equals(activePoke.getRESIST())){
      activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()/2-10); 
      System.out.println(enemyPoke.getNAME()+" has dealt "+(enemyPoke.getATKInfo().get(atknum).getDMG()/2-10)+" to "+activePoke.getNAME()+"!"); 
    }
    else{
      activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()-10);
      System.out.println(enemyPoke.getNAME()+" has dealt "+enemyPoke.getATKInfo().get(atknum-1).getDMG()+" to "+activePoke.getNAME()+"!");
    }

    if(enemyPoke.getNRG()<10){
      enemyPoke.setNRG(50);
    }
    else{
      enemyPoke.setNRG(enemyPoke.getNRG()+10);
    }  
    if(activePoke.getHP()>0){ 
      performAction(activePoke,enemyPoke);
    }
    else if(activePoke.getHP()<=0){
      System.out.println(activePoke.getNAME()+" has fainted!");
      activePoke.setDisable(false);
      team.remove(activePoke);
      if(team.size()==0){
        System.out.println("You've been defeated!");
      }
      else{
        pokeselectionafterfaint(activePoke,enemyPoke);
      }    
    }
  }

  public static void enemyrecharge(Pokemon activePoke,Pokemon enemyPoke,int atknum){
    
    if(enemyPoke.getTYPE().equals(activePoke.getWEAK())){
      activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()*2); 
      System.out.println(enemyPoke.getNAME()+" has dealt "+(enemyPoke.getATKInfo().get(atknum-1).getDMG()*2)+" to "+activePoke.getNAME()+"!");    
    }
    else if(enemyPoke.getTYPE().equals(activePoke.getRESIST())){
      activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()/2); 
      System.out.println(enemyPoke.getNAME()+" has dealt "+(enemyPoke.getATKInfo().get(atknum).getDMG()/2)+" to "+activePoke.getNAME()+"!"); 
    }
    else{
      activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(atknum).getDMG()); 
      System.out.println(enemyPoke.getNAME()+" has dealt "+enemyPoke.getATKInfo().get(atknum-1).getDMG()+" to "+activePoke.getNAME()+"!");
    }
    for(int k=0;k<team.size();k++){     
      if(50-enemyPoke.getNRG()<30){  //user replenishes a total of 30 (20 from special,10 normally)
        enemyPoke.setNRG(50);
      }
      else{
        enemyPoke.setNRG(enemyPoke.getNRG()+30); 
      }  
    }

    if(activePoke.getHP()>0){ 
      performAction(activePoke,enemyPoke);
    }
  
    else if(activePoke.getHP()<=0){
      System.out.println(activePoke.getNAME()+" has fainted!");
      team.remove(activePoke);
      if(team.size()==0){
        System.out.println("You've been defeated!");
      }
      else{
        pokeselectionafterfaint(activePoke,enemyPoke);
      }    
    }
  }

  public static void performAttack(Pokemon activePoke,Pokemon enemyPoke){
    System.out.println("1. "+activePoke.getATKInfo().get(0).getAllATKInfo());
    if(activePoke.getNumAttacks()==2){ //if pokemon has 2 attacks print info about 2nd attack
      System.out.println("2. "+activePoke.getATKInfo().get(1).getAllATKInfo());
    }
    Scanner chooseatk=new Scanner(System.in);
    int atknum=chooseatk.nextInt();
    if(activePoke.getNRG()>=activePoke.getATKInfo().get(atknum-1).getCOST()){ //check if activepoke has more energy than cost of attack
      activePoke.setNRG(activePoke.getNRG()-activePoke.getATKInfo().get(atknum-1).getCOST()); //subtracts energy accordingly for attack
      if(activePoke.getATKInfo().get(atknum-1).getSPECIAL().equals("stun")){ //if the attack has stun special
        userstunspecial(activePoke,enemyPoke,atknum-1);
      }
      if(activePoke.getATKInfo().get(atknum-1).getSPECIAL().equals("wild card")){ //if the attack has wildcard special
        userstunspecial(activePoke,enemyPoke,atknum-1);
      }
      if(activePoke.getATKInfo().get(atknum-1).getSPECIAL().equals("wild storm")){ //if the attack has wildstorm special
        userstunspecial(activePoke,enemyPoke,atknum-1);
      }
      if(activePoke.getATKInfo().get(atknum-1).getSPECIAL().equals("disable")){
        userdisablespecial(activePoke,enemyPoke,atknum-1);
      }
      if(activePoke.getATKInfo().get(atknum-1).getSPECIAL().equals("recharge")){
        userrechargespecial(activePoke,enemyPoke,atknum-1);
      }
      if(activePoke.getTYPE().equals(enemyPoke.getWEAK())){
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum-1).getDMG()*2);//double the damage if user's pokemon type is the enemy's weakness
        System.out.println(activePoke.getNAME()+" has dealt "+activePoke.getATKInfo().get(atknum-1).getDMG()*2+" to "+enemyPoke.getNAME()+"!");
      }
      else if(activePoke.getTYPE().equals(enemyPoke.getRESIST())){
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum-1).getDMG()/2);//half damage if users type is the enemy's resistance
        System.out.println(activePoke.getNAME()+" has dealt "+activePoke.getATKInfo().get(atknum-1).getDMG()/2+" to "+enemyPoke.getNAME()+"!");
      }
      else{
        enemyPoke.setHP(enemyPoke.getHP()-activePoke.getATKInfo().get(atknum-1).getDMG()); //enemy takes regular damage
        System.out.println(activePoke.getNAME()+" has dealt "+activePoke.getATKInfo().get(atknum-1).getDMG()+" to "+enemyPoke.getNAME()+"!");
      }

        
      for(int k=0;k<team.size();k++){     //all pokemon replenish 10 energy
        if(50-team.get(k).getNRG()<10){
          team.get(k).setNRG(50);
        }
        else{
        team.get(k).setNRG(team.get(k).getNRG()+10);
        }  
      }

      if(enemyPoke.getHP()>0){ //if enemypokemon is still alive, it is their turn to attack
        enemyPerformAttack(activePoke,enemyPoke);
      }
  
      else if(enemyPoke.getHP()<=0){ 
        if(pokemonlist.size()==0){
          System.out.println("You are the trainer supreme!");
        }
        else{
          System.out.println(enemyPoke.getNAME()+" has fainted!");
          pokemonlist.remove(enemyPoke);
          battle(); //start next round
        }
      }
    }
    else{
      System.out.println("Not enough energy!");
      performAction(activePoke,enemyPoke);
    }
  }

  
  public static void enemyPerformAttack(Pokemon activePoke,Pokemon enemyPoke){
    int randomattack=randint(0,1); //computer randomly picks attack
    if(enemyPoke.getNumAttacks()==1){ //if poke only has 1 possible atk
      if(enemyPoke.getNRG()>=enemyPoke.getATKInfo().get(0).getCOST()){ //check if enough energy
        enemyPoke.setNRG(enemyPoke.getNRG()-enemyPoke.getATKInfo().get(0).getCOST()); //subtracting energy
        if(enemyPoke.getATKInfo().get(0).getSPECIAL().equals("stun")){ //if the attack has stun special
          enemystun(activePoke,enemyPoke,0);
        }
        if(enemyPoke.getATKInfo().get(0).getSPECIAL().equals("wild card")){ //if the attack has wildcard special
          enemywildcard(activePoke,enemyPoke,0);
        }
        if(enemyPoke.getATKInfo().get(0).getSPECIAL().equals("wild storm")){ //if the attack has wildstorm special
          enemywildstorm(activePoke,enemyPoke,0);
        }
        if(enemyPoke.getATKInfo().get(0).getSPECIAL().equals("disable")){ //if the attack has disable special
          enemydisable(activePoke,enemyPoke,0);
        }
        if(enemyPoke.getATKInfo().get(0).getSPECIAL().equals("recharge")){ //if the attack has recharge special
          enemyrecharge(activePoke,enemyPoke,0);
        }
        if(enemyPoke.getTYPE().equals(activePoke.getWEAK())){
          activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(0).getDMG()*2);//double the damage if user's pokemon type is the enemy's weakness
        }
        else if(enemyPoke.getTYPE().equals(activePoke.getRESIST())){
          activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(0).getDMG()/2);//half damage if users type is the enemy's resistance
        }
        else{
          activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(0).getDMG()); //enemy takes regular damage
        }
        if(50-enemyPoke.getNRG()<10){ //replenishing energy
          enemyPoke.setNRG(50);
        }
        else if(50-enemyPoke.getNRG()>10){
          enemyPoke.setNRG(enemyPoke.getNRG()+10);
        }  
        System.out.println(enemyPoke.getNAME()+" has dealt "+enemyPoke.getATKInfo().get(0).getDMG()+" to "+activePoke.getNAME()+"!");
        if(activePoke.getHP()>0){ //if user pokemon still has health, it is the users turn
          performAction(activePoke,enemyPoke);
        }
        else if(activePoke.getHP()<=0){ //if pokemon has no health, proceed to choose next pokemon to deploy
          System.out.println(activePoke.getNAME()+" has fainted!");
          team.remove(activePoke);
          if(team.size()==0){
            System.out.println("You've been defeated!");
          }
          pokeselectionafterfaint(activePoke,enemyPoke);
        }
      }
      else{ //if not enough energy, treat turn as a pass
        if(50-enemyPoke.getNRG()<10){  //to prevent going over 50
          enemyPoke.setNRG(50);
        }
        else{
          enemyPoke.setNRG(enemyPoke.getNRG()+10); //regenerates 10 energy/round
        }
        performAction(activePoke,enemyPoke);
      }
    }
    if(enemyPoke.getNumAttacks()==2){ //if pokemon has 2 possible attacks, pick one of the two randomly
      if(enemyPoke.getNRG()>enemyPoke.getATKInfo().get(randomattack).getCOST()){ //check if enough energy for one of its attacks
        enemyPoke.setNRG(enemyPoke.getNRG()-enemyPoke.getATKInfo().get(randomattack).getCOST()); //subtracting energy
        if(activePoke.getATKInfo().get(randomattack).getSPECIAL().equals("stun")){ //if the attack has stun special
          userstunspecial(activePoke,enemyPoke,randomattack);
        }
        if(enemyPoke.getTYPE().equals(activePoke.getWEAK())){
          activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(randomattack).getDMG()*2);//double the damage if user's pokemon type is the enemy's weakness
        }
        else if(enemyPoke.getTYPE().equals(activePoke.getRESIST())){
          activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(randomattack).getDMG()/2);//half damage if users type is the enemy's resistance
        }
        else{
          activePoke.setHP(activePoke.getHP()-enemyPoke.getATKInfo().get(randomattack).getDMG()); //enemy takes regular damage
        }
        if(50-enemyPoke.getNRG()<10){ //replenishing energy
          enemyPoke.setNRG(50);
        }
        else if(50-enemyPoke.getNRG()>10){
          enemyPoke.setNRG(enemyPoke.getNRG()+10);
        }  
        System.out.println(enemyPoke.getNAME()+" has dealt "+enemyPoke.getATKInfo().get(1).getDMG()+" to "+activePoke.getNAME()+"!");
        if(activePoke.getHP()<=0){
          System.out.println(activePoke.getNAME()+" has fainted!");
          team.remove(activePoke);
          if(team.size()==0){
            System.out.println("You've been defeated!");
          }
          else{
            pokeselectionafterfaint(activePoke,enemyPoke);
          }
        }
        else if(activePoke.getHP()>0){
          performAction(activePoke,enemyPoke);
        }
      }
      else{ //if not enough energy, treat turn as a pass
        if(50-enemyPoke.getNRG()<10){
          enemyPoke.setNRG(50);
        }
        else{
          enemyPoke.setNRG(enemyPoke.getNRG()+10);
        }
        performAction(activePoke,enemyPoke);
      }
    }
  }
}
class Attack{
  private String atkname, special;
  private int cost,damage;
  
  public Attack(String n, int c, int d, String s){
    atkname = n;
    cost = c;
    damage = d;
    special = s;
  }
  public String getAllATKInfo(){      //methods here returned to pokemon class
    return atkname +" Cost:"+cost+" Damage:"+damage+" Special:"+special;
  }
  public String getATK(){
    return atkname;         
  }
  public int getCOST(){
    return cost;
  }
  public int getDMG(){
    return damage;
  }
  public String getSPECIAL(){
    return special;
  }
}





