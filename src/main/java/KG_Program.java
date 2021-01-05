import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;


public class KG_Program {

    private OntModel model;
    private InfModel infModel;

    private static final Logger log = LoggerFactory.getLogger(KG_Program.class);

    private static final String SOURCE = "http://semantics.id/ns/example/movie";
    private static final String NS = SOURCE + "#";

    public KG_Program() {
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    }


    public void loadMovieOntology(){
        try {
            URL urlPath = getClass().getResource("/input/movie.ttl");
            model.read(urlPath.toString(), "TTL");
            log.info("Movie Ontology successfully loaded.");
        }
        catch (Exception e) {
            log.error("Something went wrong",e);
        }
    }


    public void chooseReasoner(Scanner in) {
        System.out.println("-- Activate a reasoner");
        System.out.println("Type 0 to terminate");
        System.out.println("Type 1 for Transitive reasoner");
        System.out.println("Type 2 for RDFS rule reasoner");
        System.out.println("Type 3 for OWL reasoner");
        System.out.println("Type any other character to return");

        int userInput = 0;
        try {
            userInput = in.nextInt();
            in.nextLine();
        } catch (Exception e) {
            return;
        }

        Reasoner reasoner;

        switch (userInput) {
            case 1:
                reasoner = ReasonerRegistry.getTransitiveReasoner();
                activateReasoner(reasoner);
                break;
            case 2:
                reasoner = ReasonerRegistry.getRDFSReasoner();
                activateReasoner(reasoner);
                break;
            case 3:
                reasoner = ReasonerRegistry.getOWLReasoner();
                activateReasoner(reasoner);
                break;
            default:
        }

    }

    public void exportKnowledgeGraph(Scanner in) {
        System.out.println("-- Choose the fileName of the exported Knowledge graph");
        String fileName = in.nextLine();

        System.out.println("-- Choose the format of the exported Knowledge graph");
        System.out.println("Type 0 to terminate");
        System.out.println("Type 1 for RDFXML");
        System.out.println("Type 2 for TTL");

        int userInput = 0;
        try {
            userInput = in.nextInt();
            in.nextLine();
        } catch (Exception e) {
            return;
        }

        switch (userInput) {
            case 1:
                saveModel(Lang.RDFXML, fileName, "rdf");
                break;
            case 2:
                saveModel(Lang.TTL, fileName, "ttl");
                break;
            default:
        }

    }

    public void chooseQuery(Scanner input) {
        System.out.println("-- Choose a query");
        System.out.println("Type 1 to return all movies with their title");
        System.out.println("Type 2 to get all information about Emma Watson");
        System.out.println("Type 3 to return all actors with film studios they act in");
        System.out.println("Type 4 to select names of actors who play in movies released after 2016");
        System.out.println("Type 5 to list all actors and crews together with the title of movies that they are involved" +
                " ordered by their name");
        System.out.println("Type any other character to return");
        int userInput = 0;
        try {
            userInput = input.nextInt();
            input.nextLine();
        } catch (Exception e) {
            return;
        }

        String query = "";


        switch (userInput) {
            case 1:
                query = "PREFIX : <http://semantics.id/ns/example/movie#>\n" +
                       " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                       " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                       "\n" +
                       " SELECT *\n" +
                       " where {\n" +
                       " ?movie rdf:type :Movie .\n" +
                       " ?movie rdfs:label ?title\n" +
                       " }";
                selectQuery(query);
                break;
            case 2:
                query = "PREFIX : <http://semantics.id/ns/example/movie#>\n" +
                       "\n" +
                       "DESCRIBE ?actor where {\n" +
                       "?actor :hasName \"Emma Watson\"\n" +
                       "}";
                describeQuery(query);
                break;
            case 3:
               query = "PREFIX : <http://semantics.id/ns/example/movie#>\n" +
                       "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                       "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                       "\n" +
                       "CONSTRUCT {\n" +
                       "?actor :actsIn ?filmStudio\n" +
                       "}\n" +
                       "where {\n" +
                       "?filmStudio rdf:type :FilmStudio .\n" +
                       "?filmStudio rdfs:label ?filmStudioName .\n" +
                       "?actor rdf:type :Actor .\n" +
                       "}";
                constructQuery(query);
                break;
            case 4:
               query = "PREFIX : <http://semantics.id/ns/example/movie#>\n" +
                       "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                       "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                       "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                       "\n" +
                       "SELECT DISTINCT ?actorName\n" +
                       "where {\n" +
                       "?actor rdf:type :Actor .\n" +
                       "?actor :hasName ?actorName .\n" +
                       "?movie rdf:type :Movie .\n" +
                       "?movie :hasReleaseDate ?movieReleaseDate .\n" +
                       "?movie :hasActor ?actor\n" +
                       "FILTER (?movieReleaseDate > \"2016-01-01\"^^xsd:date)\n" +
                       "}";
                selectQuery(query);
                break;
            case 5:
                query = "PREFIX : <http://semantics.id/ns/example/movie#>\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                        "\n" +
                        "SELECT ?crewName ?movieName\n" +
                        "where {\n" +
                        "?crew :hasName ?crewName .\n" +
                        "?movie rdfs:label ?movieName\n" +
                        "{?movie :hasActor ?crew .}\n" +
                        "UNION\n" +
                        "{?movie :hasMovieDirector ?crew .}\n" +
                        "UNION\n" +
                        "{?movie :hasWriter ?crew .}\n" +
                        "} ORDER BY ?movieName";
                selectQuery(query);
                break;
            default:
        }

    }

    public void addClass(Scanner in) {
        System.out.println("-- Insert the class name");
        String className = in.nextLine();
        model.createClass(NS + className);
        log.info("Class added.");
    }

    public void addProperty(Scanner in) {
        System.out.println("-- Insert the property name");
        String propertyName = in.nextLine();

        System.out.println("-- Add domain");
        String className = in.nextLine();
        OntClass classObject = model.getOntClass(NS + className);
        if(classObject == null) {
            log.info("Given domain does not exist");
            return;
        }

        System.out.println("-- Add range");
        String rangeName = in.nextLine();
        OntClass rangeObject = model.getOntClass(NS + rangeName);
        if(rangeObject == null) {
            log.info("Given range does not exist");
            return;
        }

        ObjectProperty property = model.createObjectProperty(NS + propertyName);
        property.addDomain(classObject);
        property.addRange(classObject);
        log.info("Property added");
    }

    public void addInstance() {
        log.info("Functionality not implemented.");
    }

    public void addInstanceFromCSV(Scanner in) {
        System.out.println("-- Put the file in the input folder and enter the file name with its extension (like: 'movie-instances.ttl')");
        String userFileName = in.nextLine();

        try {
            URL urlPath = getClass().getResource("/input/" + userFileName);
            System.out.println(urlPath.toString());
            model.read(urlPath.toString(), "TTL");
            log.info("Movie instances successfully loaded.");
        }
        catch (Exception e) {
            log.error("Something went wrong",e);
        }

    }

    private void activateReasoner(Reasoner reasoner) {
        try {
            infModel = ModelFactory.createInfModel(reasoner, model);
            log.info("Reasoner successfully activated");
        }  catch (Exception e) {
            log.error("Something went wrong",e);
        }
    }

    private void saveModel(Lang exportType, String fileName, String fileExtension){
        try {
            FileOutputStream fos = new FileOutputStream("src/main/resources/output/" + fileName + "." + fileExtension);
            RDFDataMgr.write(fos, model, exportType);
            log.info("Knowledge graph successfully exported.");
            System.exit(0);
        } catch (IOException e){
            log.error("Something went wrong",e);
        }

    }

    private void selectQuery(String queryString) {
        org.apache.jena.query.ARQ.init();
        QueryExecution execution = QueryExecutionFactory.create(queryString, model);
        ResultSet resultSet = execution.execSelect();
        ResultSetFormatter.out(resultSet);
    }

    private void describeQuery(String queryString) {
        org.apache.jena.query.ARQ.init();
        QueryExecution execution = QueryExecutionFactory.create(queryString, model);
        Model model = execution.execDescribe();
        RDFDataMgr.write(System.out, model, Lang.TURTLE);
    }

    private void constructQuery(String queryString) {
        org.apache.jena.query.ARQ.init();
        QueryExecution execution = QueryExecutionFactory.create(queryString, model);
        Model model = execution.execConstruct();
        RDFDataMgr.write(System.out, model, Lang.TURTLE);
    }

}
