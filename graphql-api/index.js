const { ApolloServer } = require('@apollo/server');
const { startStandaloneServer } = require('@apollo/server/standalone');
const fetch = require('node-fetch');

const typeDefs = `#graphql
  type Book {
    title: String
    author: String
  }

  type Case {
    id: ID!
    title: String!
    description: String!
    recentHistory: [RecentHistory!]!
  }
  
  type RecentHistory {
  id: ID!
  action: String!
  description: String!
  oldValue: String
  newValue: String
  timestamp: String!
  }

  type Query {
    books: [Book]
    case(id: ID!): Case
    cases: [Case]
  }
`;

const resolvers = {
    Query: {
        case: async (_, { id }) => {
            const response = await fetch(`http://localhost:8080/api/cases/${id}`);
            return response.json();
        },
        books: () => [
            { title: '1984', author: 'George Orwell' },
            { title: 'Brave New World', author: 'Aldous Huxley' },
        ],
        cases: async () => {
            const response = await fetch('http://localhost:8080/api/cases/getAll?page=0&size=10');
            const data = await response.json();
            return Array.isArray(data.content) ? data.content : [];
        },
    },
};

const server = new ApolloServer({ typeDefs, resolvers });

startStandaloneServer(server, {
    listen: { port: 4000 },
}).then(() => {
    console.log('🚀 Server ready at http://localhost:4000');
});