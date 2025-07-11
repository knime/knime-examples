import {
  AlertingService,
  JsonDataService,
  SharedDataService,
} from "@knime/ui-extension-service";
import { arrayToTable, setInnerHTML } from "./helpers";

const setSharedData = async (jsonDataService: JsonDataService, data: any) => {
  const newData = await jsonDataService.data({
    method: "getFirstNRows",
    // @ts-ignore
    options: [data.data.view.numRows],
  }) as string[][];

  setInnerHTML(arrayToTable(newData));
};

const execute = async () => {
  const jsonDataService = await JsonDataService.getInstance();
  const inputData = await jsonDataService.initialData();
  setInnerHTML(arrayToTable(inputData));

  const sharedDataService = await SharedDataService.getInstance();
  sharedDataService.addSharedDataListener((data) => setSharedData(jsonDataService, data));

  const alertingService = await AlertingService.getInstance();
  alertingService.sendAlert({
    type: "error",
    message: "This is an example error message which will pop up when the view is opened!",
  });
};

execute();
