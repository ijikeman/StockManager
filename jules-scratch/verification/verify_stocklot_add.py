from playwright.sync_api import sync_playwright, expect

def run():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        # Navigate to the stock lot add page
        page.goto("http://localhost:5173/stocklot/add")

        # Check if the main heading is visible
        expect(page.get_by_role("heading", name="ストックロット新規登録")).to_be_visible()

        # Check if the NISA label and checkbox are visible
        nisa_label = page.get_by_text("NISA")
        expect(nisa_label).to_be_visible()

        # The checkbox is not directly associated with the label, so we find it by its role within the same table row.
        nisa_row = page.locator("tr", has=page.get_by_text("NISA"))
        nisa_checkbox = nisa_row.get_by_role("checkbox")
        expect(nisa_checkbox).to_be_visible()

        # Take a screenshot of the form
        page.screenshot(path="/app/jules-scratch/verification/stocklot_add_page.png")

        browser.close()

if __name__ == "__main__":
    run()