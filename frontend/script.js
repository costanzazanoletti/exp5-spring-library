/* CONSTANTS */
const BOOKS_API_URL = 'http://localhost:8080/api/v1/books';
const contentDOM = document.getElementById('content');
console.log(contentDOM);

/* API */
const getBooks = async () => {
  try {
    const response = await axios.get(BOOKS_API_URL);
    console.log(response);
    return response.data;
  } catch (error) {
    console.log(error);
  }
};
/* DOM MANIPULATION */
// prende in ingresso i dati e restituisce una stringa con la lista dei libri
const createBookList = (data) => {
  if (data.length > 0) {
    let list = `<ul>`;
    // itero sulla lista
    data.forEach((element) => {
      list += `<li>${element.title}</li>`;
    });
    list += `</ul>`;
    return list;
  } else {
    return `<div class="alert alert-info">Empty List</div>`;
  }
};

const loadData = async () => {
  // prendo i dati dall'api
  const data = await getBooks();
  // con quei dati costruisco il content
  // appendo il content nel DOM
  contentDOM.innerHTML = createBookList(data);
};

/* CODICE GLOBALE */
loadData();
