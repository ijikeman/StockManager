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
        isNisa: false,
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
        const payload = {
          ownerId: this.lot.owner_id,
          stockId: this.lot.stock_id,
          unit: this.lot.unit,
          price: this.lot.price,
          fee: this.lot.fees,
          isNisa: this.lot.isNisa,
          transactionDate: this.lot.date,
        };
        await axios.post("/api/stocklot/add", payload);
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