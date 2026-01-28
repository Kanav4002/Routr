import Grid from "./Grid";

export default function App() {
  return (
    <div className="min-h-screen w-full bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 flex flex-col items-center justify-start py-10 px-4">
      <h1 className="text-4xl font-bold text-center mb-8 text-white tracking-tight">
        Route Optimizer
      </h1>
      <Grid />
    </div>
  );
}
