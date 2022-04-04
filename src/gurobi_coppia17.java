import gurobi.*;

public class gurobi_coppia17
{
    public static void main(String[] args) {
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
        final double budget_percentuale_per_fascia = 0.02;
        final int copertura_giornaliera_minima = 83261;
        try
        {
            GRBEnv env = new GRBEnv("matriciopoli.log");
            setParams(env);
            GRBModel model = new GRBModel(env);
            GRBVar[][] xij = addVars(model, emittenti, fasce);
            GRBVar[] slacks = addSlackSurplusVars(model,emittenti, fasce);
            addObjectiveFunction(model, xij, spettatori_per_emittente_per_fascia, emittenti, fasce);
            addCostPerMinuteConstr(model, xij, costi_al_minuto_per_emittente_per_fascia, spesa_max_per_emittente, slacks, emittenti, fasce);
            addBudgetPerTimeSlotConstr(model, xij, costi_al_minuto_per_emittente_per_fascia, budget_percentuale_per_fascia, slacks, emittenti, fasce);
            addMinutePerBroadcasterConstr(model, xij, minuti_massimi_per_emittente_per_fascia, slacks, emittenti, fasce);
            addDailyCoverageConstr(model, xij, spettatori_per_emittente_per_fascia, copertura_giornaliera_minima, slacks, emittenti, fasce);
            resolve(model);

        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    private static void setParams(GRBEnv env) throws GRBException // Metodo per settare i parametri del solver Gurobi
    {
        env.set(GRB.IntParam.Presolve, 0); // Disattivo il presolve
        env.set(GRB.IntParam.Method, 0); // Attivo l'utilizzo del simplesso
        env.set(GRB.DoubleParam.Heuristics, 0); //Disattivo l'utilizzo delle euristiche interne
    }

    private static GRBVar[][] addVars(GRBModel model, int emittenti, int fasce) throws GRBException
    // Metodo per inserire nel modello la matrice delle variabili
    {
        GRBVar[][] xij = new GRBVar[emittenti][fasce];
        for(int i = 0; i < emittenti; i++)
        {
            for(int j = 0; j < fasce; j++)
            {
                xij[i][j] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x_" + i + "_" + j);
            }
        }
        return xij;
    }

    private static GRBVar[] addSlackSurplusVars(GRBModel model, int emittenti, int fasce) throws GRBException
    // Metodo per inserire nel modello le variabili di slack o surplus
    {
        GRBVar[] slacks = new GRBVar[emittenti + fasce];
        for(int i = 0; i < emittenti + fasce; i++)
        {
            slacks[i] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "s_" + i);
        }
        return slacks;
    }

    private static GRBVar[] addAuxiliaryVars(GRBModel model, int emittenti, int fasce) throws GRBException
    // Metodo per inserire nel modello le variabili ausiliarie
    {
        GRBVar[] aux = new GRBVar[emittenti + fasce];
        for(int i = 0; i < emittenti + fasce; i++)
        {
            aux[i] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "s_" + i);
        }
        return aux;
    }

    private static void addObjectiveFunction(GRBModel model, GRBVar[][] xij, int[][] spettatori, int emittenti, int fasce) throws GRBException
    // Metodo per inserire la funzione obiettivo nel modello
    {
        int mezzagiornata = (fasce / 2) -1; //-1 per adattare all'array che parte da 0
        GRBLinExpr obj = new GRBLinExpr();
        /* problema artificiale
        for (int i = 0; i < emittenti + fasce; i++)
        {
            obj.addTerm(1.0, aux[i]);
        }
        */
        for(int i = 0; i < emittenti; i++)
        {
            for(int j = 0; j < fasce; j++)
            {
                if (j <= mezzagiornata)
                    obj.addTerm(spettatori[i][j], xij[i][j]); //Se la fascia è tra le prime 4, sommo il valore
                else
                    obj.addTerm(-spettatori[i][j], xij[i][j]); //Se la fascia è tra le ultime 4, sottraggo il valore
            }
        }
        
        //TODO ATTENZIONE, MANCA LA SOMMA INVERSA!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        
        model.setObjective(obj);
        model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
    }

    private static void addCostPerMinuteConstr(GRBModel model, GRBVar[][] xij, int[][] costi, int[] beta, GRBVar[] slacks, int emittenti, int fasce) throws GRBException
    // Metodo per inserire i vincoli sui costi al minuto
    {
        for(int i = 0; i < emittenti; i++)
        {
            GRBLinExpr vincolo = new GRBLinExpr();
            //TODO Perche il vincolo e' nel ciclo for e non fuori come in addMinutePerBroadcasterConstr?
            for(int j = 0; j < fasce; j++)
            {
                vincolo.addTerm(costi[i][j], xij[i][j]);
            }
            //risolvo il problema in forma standard
            vincolo.addTerm(1.0, slacks[i]);
            model.addConstr(vincolo, GRB.EQUAL, beta[i], "vincolo_costi_al_minuto_" + i);
        }
    }

    private static void addBudgetPerTimeSlotConstr(GRBModel model, GRBVar[][] xij, int[][] costi, double omega, GRBVar[] slacks, int emittenti, int fasce) throws GRBException
    // Metodo per inserire i vincoli sul budget per fascia oraria
    {
        for(int i = 0; i < fasce; i++)
        {
            GRBLinExpr vincolo = new GRBLinExpr();
            for(int j = 0; j < emittenti; j++)
            {
                vincolo.addTerm(costi[j][i], xij[j][i]);
            }
            //risolvo il problema in forma standard
            vincolo.addTerm(-1.0, slacks[i]);
            model.addConstr(vincolo, GRB.EQUAL, omega, "vincolo_budget_per_fascia_" + i);
        }
    }

    private static void addMinutePerBroadcasterConstr(GRBModel model, GRBVar[][] xij, int[][] minuti_max, GRBVar[] slacks, int emittenti, int fasce) throws GRBException
    // Metodo per inserire i vincoli sui costi al minuto
    {
        GRBLinExpr vincolo = new GRBLinExpr();
        for(int i = 0; i < emittenti; i++)
        {
            for(int j = 0; j < fasce; j++)
            {
                vincolo.addTerm(1.0, xij[i][j]);
                //risolvo il problema in forma standard
                vincolo.addTerm(1.0, slacks[i]);
                model.addConstr(vincolo, GRB.EQUAL, minuti_max[i][j], "vincolo_minuti_massimi_per_fascia_ed_emittente_" + i + "_" + j);
            }
        }
    }

    private static void addDailyCoverageConstr(GRBModel model, GRBVar[][] xij, int[][] spettatori, int copertura_min, GRBVar[] slacks, int emittenti, int fasce) throws GRBException
    // Metodo per inserire i vincoli sui costi al minuto
    {
        GRBLinExpr vincolo = new GRBLinExpr();
        for(int i = 0; i < emittenti; i++)
        {
            for(int j = 0; j < fasce; j++)
            {
                vincolo.addTerm(spettatori[i][j], xij[i][j]);
            }
            //risolvo il problema in forma standard
            vincolo.addTerm(-1.0, slacks[i]); // Inserire una o tutte le variabili di slack???
        }
        model.addConstr(vincolo, GRB.EQUAL, copertura_min, "vincolo_copertura_massima_giornaliera");
    }

    private static void resolve(GRBModel model) throws GRBException
    // Metodo per la risoluzione del problema
    {
        model.optimize();
        int status = model.get(GRB.IntAttr.Status);
        System.out.println("\n\n\nStato Ottimizzazione: "+ status + "\n");
        // 2 soluzione ottima trovata
        // 3 non esiste soluzione ammissibile (infeasible)
        // 5 soluzione illimitata
        // 9 tempo limite raggiunto
    }
}
