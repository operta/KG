import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);

        KG_Program kgp = new KG_Program();
        //        1A
        kgp.loadMovieOntology();

        Boolean run = true;
        while (run) {
            System.out.println("-- Choose a task");
            System.out.println("Type 1 to activate a reasoner");
            System.out.println("Type 2 to export Knowledge Graph");
            System.out.println("Type 3 to add a new ontology class");
            System.out.println("Type 4 to add a new property");
            System.out.println("Type 5 to add a new ontology instance");System.out.println("Type 6 to add ontology instances from CSV");System.out.println("Type 7 to choose a query");System.out.println("Type any other character to terminate the program");

            int userInput = 0;
            try {
                userInput = input.nextInt();
                input.nextLine();
            } catch (Exception e) {
                System.exit(0);
            }

            switch (userInput) {
                case 1:
                    //        1B
                    kgp.chooseReasoner(input);
                    break;
                case 2:
                    //        1C
                    kgp.exportKnowledgeGraph(input);
                    break;
                case 3:
                    //        2A
                    kgp.addClass(input);
                    break;
                case 4:
                    //        2B
                    kgp.addProperty(input);
                    break;
                case 5:
                    //        2C not done
                    kgp.addInstance();
                    break;
                case 6:
                    //        2C
                    kgp.addInstanceFromCSV(input);
                    break;
                case 7:
                    //        3AB
                    kgp.chooseQuery(input);
                    break;
                default:
                    run = false;
            }
        }
    }
}
