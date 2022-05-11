package com.engie.csai.pc.core.model;

import com.engie.csai.pc.core.model.json.ClientRequestsJson;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonReaderInParallel
{


    public ClientRequestsJson parseJsonFile(String catId)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            //PDTsJson pdTsJson = mapper.readValue(Paths.get("PC_UML_IBM/Files/pdts_" + fileIndex + ".json").toFile(), PDTsJson.class);

            String fileName= "clientRequest_" + catId + ".json";
            File file = new File(fileName);
            if(file.exists()){
            ClientRequestsJson clientRequestsJson = mapper.readValue(Paths.get(fileName).toFile(), ClientRequestsJson.class);
            file.delete();
            return clientRequestsJson;
            }
            return null;



            //            //int numberOfFiles = 10;
            //            ExecutorService service = Executors.newFixedThreadPool(10);
            //            List<ReadWorker> workers = new ArrayList<>(numberOfFiles);
            //            for (int fileIndex = 0; fileIndex < numberOfFiles; fileIndex++) {
            //                workers.add(new ReadWorker(fileIndex));
            //            }
            //            List<ClientRequestsJson> toReturn = new ArrayList<>();
            //            List<Future<ClientRequestsJson>> results = service.invokeAll(workers);
            //            for (Future<ClientRequestsJson> result : results) {
            //                try {
            //                    ClientRequestsJson value = result.get();
            //                    System.out.println(value);
            //                    toReturn.add(value);
            //                } catch (ExecutionException ex) {
            //                    Logger.getLogger(JsonReaderInParallel.class.getName()).log(Level.SEVERE, null, ex);
            //                }
            //            }
            //            return toReturn;
        } catch (StreamReadException e)
        {
            e.printStackTrace();
        } catch (DatabindException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public class ReadWorker implements Callable<ClientRequestsJson>
    {

        private final int fileIndex;

        public ReadWorker(int fileIndex)
        {
            this.fileIndex = fileIndex;
        }

        @Override
        public ClientRequestsJson call() throws Exception
        {
            //            try (BufferedReader br = new BufferedReader(new FileReader("PC_UML_IBM/Files/PDT" + fileIndex + ".json"))) {
            //                StringBuilder sb = new StringBuilder();
            //                String line = br.readLine();
            //
            //                while (line != null) {
            //                    sb.append(line);
            //                    sb.append(System.lineSeparator());
            //                    line = br.readLine();
            //                }
            //                System.out.println(sb);
            //                return sb.toString();
            //            }
            ObjectMapper mapper = new ObjectMapper();

            //PDTsJson pdTsJson = mapper.readValue(Paths.get("PC_UML_IBM/Files/pdts_" + fileIndex + ".json").toFile(), PDTsJson.class);

            return mapper.readValue(Paths.get("JsonFiles/pdts_" + fileIndex + ".json").toFile(), ClientRequestsJson.class);
        }
    }
}
