import gurobi.*;
import java.util.ArrayList;

public class Gurobi_coppia_17 {
    // Costanti per l'output
    private static final String LOG_FILENAME = "matriciopoli.log";
    private static final String NUMERO_GRUPPO = "17";
    private static final String COMPONENTE_1 = "Castelnovo Luca";
    private static final String COMPONENTE_2 = "Soncina Daniele";
    private static final String MINORE = "<";
    private static final String MAGGIORE = ">";
    private static final String GRUPPO = "COPPIA " + MINORE + NUMERO_GRUPPO + MAGGIORE;
    private static final String COMPONENTI = "Componenti " + MINORE + COMPONENTE_1 + MAGGIORE + " " + MINORE + COMPONENTE_2 + MAGGIORE;
    private static final String QUESITO_1 = "QUESITO I: ";
    private static final String QUESITO_2 = "QUESITO II: ";
    private static final String QUESITO_3 = "QUESITO III: ";
    private static final String FUNZIONE_OBIETTIVO = "funzione obiettivo = ";
    private static final String COPERTURA = "copertura raggiunta totale: ";
    private static final String TEMPO = "tempo acquistato (minuti): ";
    private static final String BUDGET = "budget inutilizzato: ";
    private static final String SLACK_IN_SEGUITO = "Di seguito il valore delle slack/surplus all'ottimo dei rispettivi vincoli";
    private static final String QUADRA_APERTA = "\n[ ";
    private static final String QUADRA_CHIUSA = "]";
    private static final String SOLUZIONE_BASE_OTTIMA = "soluzione di base ottima: " + QUADRA_APERTA;
    private static final String FORMATO_NUMERO_CIFRE_DECIMALI = "%.4f";
    private static final String SPIEGAZIONE_BASI = "1 se in base, 0 altrimenti.";
    private static final String VARIABILI_IN_BASE = "variabili In base: " + QUADRA_APERTA;
    private static final String BASE = ": 1";
    private static final String NON_BASE = ": 0";
    private static final String SLACK_IN_BASE = "Slack in base: " + QUADRA_APERTA;
    private static final String COEFFICIENTI_COSTO_RIDOTTO = "coefficienti di costo ridotto: " + QUADRA_APERTA;
    private static final String COEFF_CR_SLACK = "coefficenti di costo ridotto delle slack: " + QUADRA_APERTA;
    private static final String SOLUZIONE_MULTIPLA = "soluzione ottima multipla: ";
    private static final String SOLUZIONE_DEGENERE = "soluzione ottima degenere: ";
    private static final String SI = "Si";
    private static final String NO = "No";
    private static final String VINCOLI_VERTICE_OTTIMO = "vincoli vertice ottimo: " + QUADRA_APERTA;
    private static final String VARIABILI_NON_OTTIME_UNO = "Prima soluzione non ottima selezionata: " + QUADRA_APERTA;
    private static final String VARIABILI_NON_OTTIME_DUE = "Seconda soluzione non ottima selezionata: " + QUADRA_APERTA;
    private static final String VARIABILI_NON_OTTIME_TRE = "Terza soluzione non ottima selezionata: " + QUADRA_APERTA;

    public static void main(String[] args) {
        // Costanti per la risoluzione del problema
        final int emittenti = 10;
        final int fasce = 8;
        final int[][] minuti_massimi_per_emittente_per_fascia =
                {
                        {2, 3, 2, 1, 1, 1, 1, 1},
                        {1, 2, 1, 2, 2, 2, 1, 2},
                        {3, 1, 3, 2, 1, 2, 3, 1},
                        {3, 2, 2, 2, 3, 2, 2, 2},
                        {2, 1, 2, 3, 2, 2, 3, 2},
                        {2, 3, 3, 2, 2, 3, 2, 3},
                        {1, 2, 1, 2, 2, 2, 1, 3},
                        {3, 1, 1, 2, 2, 1, 3, 3},
                        {1, 1, 2, 2, 1, 2, 3, 2},
                        {2, 2, 3, 2, 2, 3, 1, 2}
                };
        final int[][] costi_al_minuto_per_emittente_per_fascia =
                {
                        {1119, 1391, 1389, 915, 1025, 1220, 1239, 984},
                        {1352, 1319, 913, 975, 1311, 1139, 1218, 1007},
                        {1112, 927, 1086, 918, 1069, 1327, 1096, 1388},
                        {1131, 1240, 994, 1056, 1326, 1324, 1329, 1004},
                        {1196, 1225, 1209, 969, 1370, 1167, 1293, 929},
                        {1036, 1022, 1341, 1279, 1218, 1058, 1289, 1214},
                        {1253, 1099, 1032, 943, 904, 921, 1104, 1119},
                        {1081, 1008, 1118, 1088, 1291, 965, 1251, 1117},
                        {909, 1072, 1145, 1232, 963, 1176, 1269, 962},
                        {1001, 1279, 964, 1271, 1391, 1380, 1109, 1122}
                };
        final int[][] spettatori_per_emittente_per_fascia =
                {
                        {3102, 534, 1866, 2592, 2432, 806, 399, 2704},
                        {1295, 3329, 2278, 368, 904, 2382, 569, 3406},
                        {2075, 3420, 3017, 3094, 3390, 1494, 3070, 2993},
                        {2347, 1098, 557, 724, 2346, 903, 1740, 3078},
                        {534, 3289, 2955, 339, 1282, 911, 1141, 3393},
                        {627, 2366, 1002, 2643, 1543, 3161, 2776, 3173},
                        {2728, 406, 3290, 1430, 2394, 3203, 1873, 1330},
                        {1183, 691, 417, 775, 3295, 1321, 903, 1584},
                        {812, 1310, 2162, 430, 339, 2193, 3359, 2815},
                        {3032, 3431, 2983, 2105, 2986, 2929, 2625, 2560}
                };
        final int[] spesa_max_per_emittente = {2534, 2794, 2967, 2525, 3159, 2983, 3267, 2521, 3231, 3262};
        final double budget_percentuale = 0.02;
        final int copertura_giornaliera_minima = 83261;
        try {
            //Creo l'enviroment e imposto i settaggi desiderati
            GRBEnv env = new GRBEnv(LOG_FILENAME);
            setParams(env);
            //Creo il modello, le variabili e imposto la funzione obiettivo e i vincoli del problema
            GRBModel model = new GRBModel(env);
            GRBVar[][] xij = addVars(model, emittenti, fasce);
            double copertura_raggiunta, minuti_acquistati, budget_inutilizzato;
            addObjectiveFunction(model, xij, spettatori_per_emittente_per_fascia, emittenti, fasce);
            addCostPerMinuteConstr(model, xij, costi_al_minuto_per_emittente_per_fascia, spesa_max_per_emittente, emittenti, fasce);
            addBudgetPerTimeSlotConstr(model, xij, costi_al_minuto_per_emittente_per_fascia, budget_percentuale, spesa_max_per_emittente, emittenti, fasce);
            addMinutePerBroadcasterConstr(model, xij, minuti_massimi_per_emittente_per_fascia, emittenti, fasce);
            addDailyCoverageConstr(model, xij, spettatori_per_emittente_per_fascia, copertura_giornaliera_minima, emittenti, fasce);
            addPositiveConstr(model, xij, emittenti, fasce);
            //avvio la risoluzione del problema
            resolve(model);
            //calcolo alcune informazioni utili alle consegne
            copertura_raggiunta = calculateReachedCoverage(xij, spettatori_per_emittente_per_fascia, emittenti, fasce);
            minuti_acquistati = calculateBoughtMinutes(xij, emittenti, fasce);
            budget_inutilizzato = calculateUselessBudget(spesa_max_per_emittente, costi_al_minuto_per_emittente_per_fascia, xij, emittenti, fasce);
            //Mostro l'intestazione dell'output dei quesiti
            showProjectGeneralities();
            //Primo quesito
            elaborateFirstAnswer(model, copertura_raggiunta, minuti_acquistati, budget_inutilizzato);
            //Secondo quesito
            elaborateSecondAnswer(model);
            //Terzo quesito
            elaborateThirdAnswer(env, spettatori_per_emittente_per_fascia, costi_al_minuto_per_emittente_per_fascia, minuti_massimi_per_emittente_per_fascia,
                    spesa_max_per_emittente, budget_percentuale, copertura_giornaliera_minima, emittenti, fasce);
            // Libera le risorse occupate dal modello e dal enviroment
            model.dispose();
            env.dispose();
            // FINE
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    private static void setParams(GRBEnv env)
            throws GRBException // Metodo per settare i parametri del solver Gurobi
    {
        env.set(GRB.IntParam.Presolve, 0); // Disattivo il presolve
        env.set(GRB.IntParam.Method, 0); // Attivo l'utilizzo del simplesso
        env.set(GRB.DoubleParam.Heuristics, 0); //Disattivo l'utilizzo delle euristiche interne
    }

    private static GRBVar[][] addVars(GRBModel model,
                                      int emittenti,
                                      int fasce)
            throws GRBException
    // Metodo per inserire nel modello la matrice delle variabili
    {
        GRBVar[][] xij = new GRBVar[emittenti][fasce];
        for (int i = 0; i < emittenti; i++) {
            for (int j = 0; j < fasce; j++) {
                xij[i][j] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x_" + i + "_" + j);
            }
        }
        return xij;
    }

    private static void addObjectiveFunction(GRBModel model,
                                             GRBVar[][] xij,
                                             int[][] spettatori,
                                             int emittenti,
                                             int fasce)
            throws GRBException
    // Metodo per inserire la funzione obiettivo nel modello
    {
        int mezzagiornata = (fasce / 2) - 1; //-1 per adattare all'array che parte da 0
        int modulo_plus = 0, modulo_minus = 0;
        GRBLinExpr obj_plus = new GRBLinExpr();
        GRBLinExpr obj_minus = new GRBLinExpr();
        for (int i = 0; i < emittenti; i++) {
            for (int j = 0; j < fasce; j++) {
                if (j <= mezzagiornata) {
                    modulo_plus += spettatori[i][j];
                    modulo_minus += -spettatori[i][j];
                    obj_plus.addTerm(spettatori[i][j], xij[i][j]); //Se la fascia è tra le prime 4, sommo il valore
                    obj_minus.addTerm(-spettatori[i][j], xij[i][j]); // Costruisco anche la funzione obiettivo opposta
                } else {
                    modulo_plus += -spettatori[i][j];
                    modulo_minus += spettatori[i][j];
                    obj_plus.addTerm(-spettatori[i][j], xij[i][j]); //Se la fascia è tra le ultime 4, sottraggo il valore
                    obj_minus.addTerm(spettatori[i][j], xij[i][j]);
                }
            }
        }
        if (modulo_plus >= modulo_minus) // Uso come funzione obbiettivo quella col coefficiente massimo (In pratica, applico il valore assoluto)
            model.setObjective(obj_plus);
        else
            model.setObjective(obj_minus);
        model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE); //Imposto di trovare il minimo per la funzione obbiettivo
    }

    private static void addCostPerMinuteConstr(GRBModel model,
                                               GRBVar[][] xij,
                                               int[][] costi,
                                               int[] beta,
                                               int emittenti,
                                               int fasce)
            throws GRBException
    // Metodo per inserire i vincoli sui costi al minuto
    {
        for (int i = 0; i < emittenti; i++) {
            GRBLinExpr vincolo = new GRBLinExpr();
            for (int j = 0; j < fasce; j++) {
                vincolo.addTerm(costi[i][j], xij[i][j]);
            }
            model.addConstr(vincolo, GRB.LESS_EQUAL, beta[i], "vincolo_costi_al_minuto_" + i);
        }
    }

    private static void addBudgetPerTimeSlotConstr(GRBModel model,
                                                   GRBVar[][] xij,
                                                   int[][] costi,
                                                   double omega,
                                                   int[] beta,
                                                   int emittenti,
                                                   int fasce)
            throws GRBException
    // Metodo per inserire i vincoli sul budget per fascia oraria
    {
        int budget_totale = calculateMaxBudget(beta);
        double budget_calcolato = omega * budget_totale;
        for (int i = 0; i < fasce; i++) {
            GRBLinExpr vincolo = new GRBLinExpr();
            for (int j = 0; j < emittenti; j++) {
                vincolo.addTerm(costi[j][i], xij[j][i]);
            }
            model.addConstr(vincolo, GRB.GREATER_EQUAL, budget_calcolato, "vincolo_budget_per_fascia_" + i);
        }
    }

    private static void addMinutePerBroadcasterConstr(GRBModel model,
                                                      GRBVar[][] xij,
                                                      int[][] minuti_max,
                                                      int emittenti,
                                                      int fasce)
            throws GRBException
    // Metodo per inserire i vincoli sui costi al minuto
    {
        for (int i = 0; i < emittenti; i++) {
            for (int j = 0; j < fasce; j++) {
                GRBLinExpr vincolo = new GRBLinExpr();
                vincolo.addTerm(1.0, xij[i][j]);
                model.addConstr(vincolo, GRB.LESS_EQUAL, minuti_max[i][j], "vincolo_minuti_massimi_per_fascia_ed_emittente_" + i + "_" + j);
            }
        }
    }

    private static void addDailyCoverageConstr(GRBModel model,
                                               GRBVar[][] xij,
                                               int[][] spettatori,
                                               int copertura_min,
                                               int emittenti,
                                               int fasce)
            throws GRBException
    // Metodo per inserire i vincoli sui costi al minuto
    {
        GRBLinExpr vincolo = new GRBLinExpr();
        for (int i = 0; i < emittenti; i++) {
            for (int j = 0; j < fasce; j++) {
                vincolo.addTerm(spettatori[i][j], xij[i][j]);
            }
        }
        model.addConstr(vincolo, GRB.GREATER_EQUAL, copertura_min, "vincolo_copertura_massima_giornaliera");
    }

    private static void addPositiveConstr(GRBModel model,
                                          GRBVar[][] xij,
                                          int emittenti,
                                          int fasce)
            throws GRBException
    {
        for (int i = 0; i < emittenti; i++) {
            for (int j = 0; j < fasce; j++) {
                GRBLinExpr vincolo = new GRBLinExpr();
                vincolo.addTerm(1.0, xij[i][j]);
                model.addConstr(vincolo, GRB.GREATER_EQUAL, 0.0, "vincolo_variabil_maggiori_di_zero_" + i + "_" + j);
            }
        }
    }

    private static void resolve(GRBModel model)
            throws GRBException
    // Metodo per la risoluzione del problema
    {
        model.optimize();
        int status = model.get(GRB.IntAttr.Status);
        System.out.println("\nStato Ottimizzazione: "+ status + "\n");
        // 2 soluzione ottima trovata
        // 3 non esiste soluzione ammissibile (infeasible)
        // 5 soluzione illimitata
        // 9 tempo limite raggiunto
    }
    private static int calculateMaxBudget(int[] beta)
    {
        //Metodo per calcolare il massimo budget a disposizione
        int budget_totale = 0;
        for (int i = 0; i < beta.length; i++)
        {
            budget_totale += beta[i];
        }
        return budget_totale;
    }

    private static double calculateReachedCoverage(GRBVar[][] xij,
                                                   int[][] spettatori,
                                                   int emittenti,
                                                   int fasce) throws GRBException
    {
        //Metodo per calcolare la copertura raggiunta all'ottimo
        double copertura = 0.0;
        for (int i = 0; i < emittenti; i++) {
            for (int j = 0; j < fasce; j++) {
                copertura += + (spettatori[i][j] * xij[i][j].get(GRB.DoubleAttr.X));
            }
        }
        return copertura;
    }

    private static double calculateBoughtMinutes(GRBVar[][] xij,
                                                 int emittenti,
                                                 int fasce)
            throws GRBException
    {
        //Metodo per calcolare i minuti comprati all'ottimo
        double minuti_comprati = 0;
        for (int i = 0; i < emittenti; i++)
        {
            for (int j = 0; j < fasce; j++)
            {
                minuti_comprati += (xij[i][j].get(GRB.DoubleAttr.X));
            }
        }
        return minuti_comprati;
    }

    private static double calculateUselessBudget(int[] budget,
                                                 int[][] costi,
                                                 GRBVar[][] xij,
                                                 int emittenti,
                                                 int fasce)
            throws GRBException
    {
        //Metodo per calcolare il budget inutilizzato all'ottimo
        int budget_totale = calculateMaxBudget(budget);
        int spesa_effettuata = 0;

        for (int i = 0; i < emittenti; i++)
        {
            for (int j = 0; j < fasce; j++)
            {
                spesa_effettuata += (costi[i][j] * xij[i][j].get(GRB.DoubleAttr.X));
            }
        }
        int budget_inutilizzato = budget_totale - spesa_effettuata;
        return budget_inutilizzato;
    }

    private static void showProjectGeneralities()
    {
        //Mostra solo la prima intestazione dell'output
        System.out.println("\n" + GRUPPO + "\n" + COMPONENTI + "\n");
    }

    private static void elaborateFirstAnswer(GRBModel model,
                                             double copertura,
                                             double minuti,
                                             double budget)
            throws  GRBException
    {
        String funzione_obiettivo = String.format(FORMATO_NUMERO_CIFRE_DECIMALI, model.get(GRB.DoubleAttr.ObjVal));
        String copertura_raggiunta = String.format(FORMATO_NUMERO_CIFRE_DECIMALI, copertura);
        String minuti_acquistati = String.format(FORMATO_NUMERO_CIFRE_DECIMALI, minuti);
        String budget_inutilizzato = String.format(FORMATO_NUMERO_CIFRE_DECIMALI, budget);
        System.out.println(QUESITO_1);
        System.out.println(FUNZIONE_OBIETTIVO + " " + MINORE + funzione_obiettivo + MAGGIORE);
        System.out.println(COPERTURA + " " + MINORE + copertura_raggiunta + MAGGIORE);
        System.out.println(TEMPO + " " + MINORE + minuti_acquistati + MAGGIORE);
        System.out.println(BUDGET + " " + MINORE + budget_inutilizzato + MAGGIORE);
        System.out.println(SOLUZIONE_BASE_OTTIMA);
        for(GRBVar var : model.getVars())
        {
            //stampo il valore delle variabili all'ottimo
            System.out.println(MINORE + var.get(GRB.StringAttr.VarName) + MAGGIORE + " = " + MINORE + String.format(FORMATO_NUMERO_CIFRE_DECIMALI, var.get(GRB.DoubleAttr.X)) + MAGGIORE);
        }
        //stampo il valore ottimo delle slack/surplus del problema
        System.out.println("\n" + SLACK_IN_SEGUITO + "\n");
		for(GRBConstr c: model.getConstrs())
		{
			System.out.println(MINORE + c.get(GRB.StringAttr.ConstrName) + MAGGIORE + ": " + MINORE + String.format(FORMATO_NUMERO_CIFRE_DECIMALI, c.get(GRB.DoubleAttr.Slack)) + MAGGIORE);
		}
        System.out.println(("\n"));
    }

    private static void elaborateSecondAnswer(GRBModel model)
            throws GRBException {
        System.out.println(QUESITO_2);
        System.out.println(VARIABILI_IN_BASE);
        System.out.println(SPIEGAZIONE_BASI);
        for(GRBVar var : model.getVars())
        //Stampo sa video le variabili specificando se sono in base o meno
        {
            try
            {
                if(var.get(GRB.IntAttr.VBasis) == 0)
                    System.out.println(MINORE + var.get(GRB.StringAttr.VarName) + BASE + MAGGIORE);
                else
                    System.out.println(MINORE + var.get(GRB.StringAttr.VarName) + NON_BASE + MAGGIORE);
            }
            catch (GRBException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println(QUADRA_CHIUSA);
        System.out.println(SLACK_IN_BASE);
        System.out.println(SPIEGAZIONE_BASI);
        for(GRBConstr constr : model.getConstrs())
        {
            //Faccio lo stesso con le variabili di slack
            try
            {
                if (constr.get(GRB.IntAttr.CBasis) == 0)
                    System.out.println(MINORE + constr.get(GRB.StringAttr.ConstrName) + BASE + MAGGIORE);
                else
                    System.out.println(MINORE + constr.get(GRB.StringAttr.ConstrName) + NON_BASE + MAGGIORE);
            }

            catch (GRBException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println(QUADRA_CHIUSA);
        //Contatore per determinare se la soluzione è multipla
        int counter = 0;
        ArrayList<String> coefficienti_costo_ridotto = new ArrayList<String>();
        //Ciclo per determinare i coefficienti di costo ridotto delle variabili
        for(GRBVar var : model.getVars())
        {
            coefficienti_costo_ridotto.add(String.format(FORMATO_NUMERO_CIFRE_DECIMALI, var.get(GRB.DoubleAttr.RC)));
            if(var.get(GRB.DoubleAttr.RC) == 0)
            {
                counter++;
            }
        }
        //Ciclo per determinare i coefficienti di costo ridotto delle slack
        for(GRBConstr constr : model.getConstrs())
        {
            double pi = - constr.get(GRB.DoubleAttr.Pi);
            String coefficiente = String.format(FORMATO_NUMERO_CIFRE_DECIMALI,pi);

            if(pi == 0)
            {
                counter++;
            }
            coefficienti_costo_ridotto.add(coefficiente);
        }
        System.out.println("\n" + COEFFICIENTI_COSTO_RIDOTTO);
        int i = 0;
        for(String coeff : coefficienti_costo_ridotto)
        {
            i++;
            if (i == 80)
            {
                System.out.println(MINORE + coeff + MAGGIORE);
                System.out.println(QUADRA_CHIUSA);
                System.out.println(COEFF_CR_SLACK);
            }
            else
                System.out.println(MINORE + coeff + MAGGIORE);
        }
        System.out.println(QUADRA_CHIUSA + "\n");
        //Calcolo soluzione multipla
        if(model.getConstrs().length == counter)
            System.out.println(SOLUZIONE_MULTIPLA + MINORE + NO + MAGGIORE);
        else
            System.out.println(SOLUZIONE_MULTIPLA + MINORE + SI + MAGGIORE);
        //Calcolo soluzione degenere
        boolean degenere = false;
        for (GRBVar var : model.getVars())
        {
            if (var.get(GRB.DoubleAttr.X) == 0)
                degenere = true;
        }
        if(degenere)
            System.out.println(SOLUZIONE_DEGENERE + MINORE + SI + MAGGIORE);
        else
            System.out.println(SOLUZIONE_DEGENERE + MINORE + NO + MAGGIORE);
        System.out.println("\n" + VINCOLI_VERTICE_OTTIMO);
        //Identificazione vertice ottimo
        for(GRBConstr constr : model.getConstrs())
        {
            if(constr.get(GRB.DoubleAttr.Slack) == 0)
            {
               System.out.println(MINORE + constr.get(GRB.StringAttr.ConstrName) + MAGGIORE);
            }
        }
        System.out.println(QUADRA_CHIUSA + "\n");
    }

    private static void elaborateThirdAnswer(GRBEnv env,
                                            int[][] spettatori_per_emittente_per_fascia,
                                            int[][] costi_al_minuto_per_emittente_per_fascia,
                                            int[][] minuti_massimi_per_emittente_per_fascia,
                                            int[] spesa_max_per_emittente,
                                            double budget_percentuale,
                                            int copertura_giornaliera_minima,
                                            int emittenti,
                                            int fasce
    ) throws GRBException
    {
        System.out.println(QUESITO_3);
        GRBModel firstModelNotOptimal= new GRBModel(env);
        GRBModel secondModelNotOptimal= new GRBModel(env);
        GRBModel thirdModelNotOptimal= new GRBModel(env);

        //Prima soluzione non ottima: Selezioniamo la funzione obiettivo scartata dal valore assoluto dal modello ottimo
        GRBVar[][] xij1 = addVars(firstModelNotOptimal, emittenti, fasce);
        addNotOptimalObjectiveFunction(firstModelNotOptimal, xij1, spettatori_per_emittente_per_fascia, emittenti, fasce);
        addCostPerMinuteConstr(firstModelNotOptimal, xij1, costi_al_minuto_per_emittente_per_fascia, spesa_max_per_emittente, emittenti, fasce);
        addBudgetPerTimeSlotConstr(firstModelNotOptimal, xij1, costi_al_minuto_per_emittente_per_fascia, budget_percentuale, spesa_max_per_emittente, emittenti, fasce);
        addMinutePerBroadcasterConstr(firstModelNotOptimal, xij1, minuti_massimi_per_emittente_per_fascia, emittenti, fasce);
        addDailyCoverageConstr(firstModelNotOptimal, xij1, spettatori_per_emittente_per_fascia, copertura_giornaliera_minima, emittenti, fasce);
        addPositiveConstr(firstModelNotOptimal, xij1, emittenti, fasce);
        System.out.println(VARIABILI_NON_OTTIME_UNO);
        resolve(firstModelNotOptimal);
        for(GRBVar var : firstModelNotOptimal.getVars())
        {
            //stampo il valore delle variabili della soluzione non ottima trovata
            System.out.println(MINORE + var.get(GRB.StringAttr.VarName) + MAGGIORE + " = " + MINORE + String.format(FORMATO_NUMERO_CIFRE_DECIMALI, var.get(GRB.DoubleAttr.X)) + MAGGIORE);
        }
        System.out.println(QUADRA_CHIUSA +"\n");

        //Seconda soluzione non ottima: Imposto un vincolo più restrittivo - In questo caso omega = 0.03 anzichè 0.02
        double omega_ristretto = 0.03;
        GRBVar[][] xij2 = addVars(secondModelNotOptimal, emittenti, fasce);
        addObjectiveFunction(secondModelNotOptimal, xij2, spettatori_per_emittente_per_fascia, emittenti, fasce);
        addCostPerMinuteConstr(secondModelNotOptimal, xij2, costi_al_minuto_per_emittente_per_fascia, spesa_max_per_emittente, emittenti, fasce);
        addBudgetPerTimeSlotConstr(secondModelNotOptimal, xij2, costi_al_minuto_per_emittente_per_fascia, omega_ristretto, spesa_max_per_emittente, emittenti, fasce);
        addMinutePerBroadcasterConstr(secondModelNotOptimal, xij2, minuti_massimi_per_emittente_per_fascia, emittenti, fasce);
        addDailyCoverageConstr(secondModelNotOptimal, xij2, spettatori_per_emittente_per_fascia, copertura_giornaliera_minima, emittenti, fasce);
        addPositiveConstr(secondModelNotOptimal, xij2, emittenti, fasce);
        System.out.println(VARIABILI_NON_OTTIME_DUE);
        resolve(secondModelNotOptimal);
        for(GRBVar var : secondModelNotOptimal.getVars())
        {
            //stampo il valore delle variabili della soluzione non ottima trovata
            System.out.println(MINORE + var.get(GRB.StringAttr.VarName) + MAGGIORE + " = " + MINORE + String.format(FORMATO_NUMERO_CIFRE_DECIMALI, var.get(GRB.DoubleAttr.X)) + MAGGIORE);
        }
        System.out.println(QUADRA_CHIUSA +"\n");

        //Terza soluzione non ottima: Abbiamo imposto che un valore della matrice dei minuti (i = 1, j = 1) debba essere per forza uguale a 1.0
        GRBVar[][] xij3 = addVars(thirdModelNotOptimal, emittenti, fasce);
        addObjectiveFunction(thirdModelNotOptimal, xij3, spettatori_per_emittente_per_fascia, emittenti, fasce);
        addCostPerMinuteConstr(thirdModelNotOptimal, xij3, costi_al_minuto_per_emittente_per_fascia, spesa_max_per_emittente, emittenti, fasce);
        addBudgetPerTimeSlotConstr(thirdModelNotOptimal, xij3, costi_al_minuto_per_emittente_per_fascia, budget_percentuale, spesa_max_per_emittente, emittenti, fasce);
        addRestrictedMinutePerBroadcasterConstr(thirdModelNotOptimal, xij3, minuti_massimi_per_emittente_per_fascia, emittenti, fasce);
        addDailyCoverageConstr(thirdModelNotOptimal, xij3, spettatori_per_emittente_per_fascia, copertura_giornaliera_minima, emittenti, fasce);
        addPositiveConstr(thirdModelNotOptimal, xij3, emittenti, fasce);
        System.out.println(VARIABILI_NON_OTTIME_TRE);
        resolve(thirdModelNotOptimal);
        for(GRBVar var : thirdModelNotOptimal.getVars())
        {
            //stampo il valore delle variabili della soluzione non ottima trovata
            System.out.println(MINORE + var.get(GRB.StringAttr.VarName) + MAGGIORE + " = " + MINORE + String.format(FORMATO_NUMERO_CIFRE_DECIMALI, var.get(GRB.DoubleAttr.X)) + MAGGIORE);
        }
        System.out.println(QUADRA_CHIUSA +"\n");
    }

    private static void addNotOptimalObjectiveFunction(GRBModel model,
                                                       GRBVar[][] xij,
                                                       int[][] spettatori,
                                                       int emittenti,
                                                       int fasce)
            throws GRBException
    // Metodo per inserire una funzione obiettivo non ottima per la risoluzione del problema
    // Il modulo che seleziona il valore assoluto positivo per il problema di ricerca dell'ottimo viene invertito
    {
        int mezzagiornata = (fasce / 2) - 1; // -1 per adattare all'array che parte da 0
        int modulo_plus = 0, modulo_minus = 0;
        GRBLinExpr obj_plus = new GRBLinExpr();
        GRBLinExpr obj_minus = new GRBLinExpr();
        for(int i = 0; i < emittenti; i++)
        {
            for(int j = 0; j < fasce; j++)
            {
                if (j <= mezzagiornata)
                {
                    modulo_plus += spettatori[i][j];
                    modulo_minus += -spettatori[i][j];
                    obj_plus.addTerm(spettatori[i][j], xij[i][j]); //Se la fascia è tra le prime 4, sommo il valore
                    obj_minus.addTerm(-spettatori[i][j], xij[i][j]); // Costruisco anche la funzione obiettivo opposta
                }
                else
                {
                    modulo_plus += -spettatori[i][j];
                    modulo_minus += spettatori[i][j];
                    obj_plus.addTerm(-spettatori[i][j], xij[i][j]); //Se la fascia è tra le ultime 4, sottraggo il valore
                    obj_minus.addTerm(spettatori[i][j], xij[i][j]);
                }
            }
        }
        if (modulo_plus < modulo_minus) // Seleziono il valore non ottimo
            model.setObjective(obj_plus);
        else
            model.setObjective(obj_minus);
        model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE); //Imposto di trovare il minimo per la funzione obiettivo
    }

    private static void addRestrictedMinutePerBroadcasterConstr(GRBModel model,
                                                                GRBVar[][] xij,
                                                                int[][] minuti_max,
                                                                int emittenti,
                                                                int fasce)
            throws GRBException
    // Metodo per inserire i vincoli sui costi al minuto
    {
        for (int i = 0; i < emittenti; i++) {
            for (int j = 0; j < fasce; j++) {
                GRBLinExpr vincolo = new GRBLinExpr();
                vincolo.addTerm(1.0, xij[i][j]);
                if(i == 1 && j == 1)
                    model.addConstr(vincolo, GRB.EQUAL, 1.0, "vincolo_minuti_massimi_per_fascia_ed_emittente_" + i + "_" + j);
                else
                    model.addConstr(vincolo, GRB.LESS_EQUAL, minuti_max[i][j], "vincolo_minuti_massimi_per_fascia_ed_emittente_" + i + "_" + j);
            }
        }
    }
}
