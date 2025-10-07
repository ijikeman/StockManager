<template src="./templates/Add.html"></template>

<script>
import axios from "axios";

export default {
  name: "StockLotAdd",
  data() {
    return {
      lot: {
        date: new Date().toISOString().slice(0, 10),
        type: "buy",
        stock_id: null,
        owner_id: null,
        unit: 0,
        price: 0,
        fees: 0,
      },
      stocks: [],
      owners: [],
    };
  },
  methods: {
    async fetchStocks() {
      try {
        const response = await axios.get("/api/stock");
        this.stocks = response.data;
      } catch (error) {
        console.error("Error fetching stocks:", error);
      }
    },
    async fetchOwners() {
      try {
        const response = await axios.get("/api/owner");
        this.owners = response.data;
      } catch (error) {
        console.error("Error fetching owners:", error);
      }
    },
    async submitForm() {
      try {
        await axios.post("/api/stocklot", this.lot);
        this.$router.push("/stocklot");
      } catch (error) {
        console.error("Error creating stock lot:", error);
      }
    },
  },
  mounted() {
    this.fetchStocks();
    this.fetchOwners();
  },
};
</script>