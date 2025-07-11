export const arrayToTable = (data: string[][]): string => {
  let table = "<table>";
  for (const row of data) { 
    table += "<tr>";
    for (const cell of row) {
      table += `<td>${cell}</td>`;
    }
    table += "</tr>";
  }
  table += "</table>";
  return table;
};

export const setInnerHTML = (innerHTML: string) => {
  document.querySelector<HTMLDivElement>("#app")!.innerHTML = `
      <div>
        ${innerHTML}
      </div>
    `;
}