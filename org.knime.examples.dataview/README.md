### Frontend

- Install the necessary dependencies: `npm i`
- Needed package: `@knime/ui-extension-service`
- Build the project: `npm run build`
- Run the dev mode: `npm run dev`
- Dev tools ```-Dorg.knime.ui.dev.mode=true``` (will display two buttons, the left will open the console, thr right one reloads the view)
- Hot-code reloading: `-Dorg.knime.ui.dev.node.view.url=http://localhost:5173/DataView.html` (the address should be the same as is used by `npm run dev`)

### Quirks

- If the build JS files are part of the plugin, the MANIFEST.MF must contain `Eclipse-BundleShape: dir` <br>
  Otherwise, the javascript files would not be respected since the project would be packed into a single jar
- When using a build tool, paths in the files of the dist folder must start with a `./` instead of `/` (e.g. the vite config must specify the base path when using vite as build tool `export default defineConfig({ base: './' });`)

### Iterations

#### Iteration 1: View from String Supplier

```
.addView(v -> v//
    .withoutSettings()//
    .description("The view node description")//
    .page(p -> p//
        .fromString(() -> "Hello World!")//
        .relativePath("abc.html"))//
)//
```

#### Iteration 2: View from build JS files

```
.addView(v -> v//
    .withoutSettings()//
    .description("The view node description")//
    .page(p -> p//
        .fromFile()//
        .bundleClass(DataViewNodeFactory.class)//
        .basePath("js-src/dist")//
        .relativeFilePath("index.html")//
        .addResourceDirectory("assets"))//
)//
```

#### Iteration 3: View with initial data (depending on view settings)

Backend

```
.addView(v -> v//
    .settingsClass(DataViewViewSettings.class)//
    .description("The view node description")//
    .page(p -> p//
        .fromFile()//
        .bundleClass(DataViewNodeFactory.class)//
        .basePath("js-src/dist")//
        .relativeFilePath("index.html")//
        .addResourceDirectory("assets"))//
    .initialData(rid -> rid//
        .data(vi -> {
            final var table = vi.getInternalTables()[0];
            final var settings = (DataViewViewSettings)vi.getSettings();
            return extractTableContent(table, settings.m_numRows);
        })//
    )//
)//
```

Frontend

```
import { arrayToTable, setInnerHTML } from "./helpers";
import { JsonDataService } from "@knime/ui-extension-service";

const execute = async () => {
    const jsonDataService = await JsonDataService.getInstance();
    const data = await jsonDataService.initialData();

    setInnerHTML(arrayToTable(data));
}
```
