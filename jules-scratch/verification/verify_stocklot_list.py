from playwright.sync_api import sync_playwright, expect

def run():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        # Navigate to the stock lot list page
        page.goto("http://localhost:5173/stocklot")

        # Check if the main heading is visible
        expect(page.get_by_role("heading", name="ストックロット一覧")).to_be_visible()

        # Wait for the table to be populated
        # We can check for a specific table header to ensure the page has loaded
        expect(page.get_by_role("cell", name="銘柄コード")).to_be_visible()
        expect(page.get_by_role("cell", name="オーナー")).to_be_visible()
        expect(page.get_by_role("cell", name="単元数")).to_be_visible()
        expect(page.get_by_role("cell", name="現在価格")).to_be_visible()

        # Take a screenshot of the list page
        page.screenshot(path="/app/jules-scratch/verification/stocklot_list_page.png")

        browser.close()

if __name__ == "__main__":
    run()